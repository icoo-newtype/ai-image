<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useKeyEvent } from '@/hooks/useKeyEvent';
import { useModalStack } from '@/store/modalStack';

const props = defineProps<{
  title?: string;
  modelValue: boolean;
  preventCloseOnBackdrop?: boolean;
  preventCloseOnEsc?: boolean;
  hideHeader?: boolean;
  hideHeaderClose?: boolean;
  size?: 'sm' | 'md' | 'lg' | 'xl';
}>();

const emits = defineEmits<{
  'update:modelValue': [boolean];
  'changed': [boolean]
}>();

const close = () => {
  emits('update:modelValue', false);
};

const modalStack = useModalStack();
const modalKey = ref();
const body = document.querySelector('body') as HTMLElement;

const modalVisible = ref(false);
const exec = async () => {
  if (props.modelValue) {
    modalKey.value = modalStack.open();
    modalVisible.value = true;
    await nextTick();
  } else {
    modalStack.close(modalKey.value);
  }
  const modal = el.value.querySelector('.modal') as HTMLElement;
  const backdrop = el.value.querySelector('.modal-backdrop') as HTMLElement;
  if (props.modelValue) {
    void modal.offsetWidth;
    void backdrop.offsetWidth;
    body.classList.add('modal-open');
    modal.classList.add('show');
    backdrop.classList.add('show');
  } else {
    body.classList.remove('modal-open');
    modal.classList.remove('show');
    backdrop.classList.remove('show');
    modal.addEventListener('transitionend', () => {
      modalVisible.value = false;
    }, { once: true });
  }
};
const el = ref();
watch(() => props.modelValue, exec);
watch(modalVisible, (v) => {
  emits('changed', v);
});

const clickBackdrop = () => {
  if (props.preventCloseOnBackdrop) return;
  close();
};

onMounted(() => {
  if (props.modelValue) exec();
});
onBeforeUnmount(()=> {
  body.classList.remove('modal-open');
})


useKeyEvent((e) => {
  if (e.key === 'Escape') {
    if (!modalStack.check(modalKey.value)) return;
    if (props.preventCloseOnEsc) return;
    close();
  }
});
</script>

<template>
  <div class="modal-outer" v-if="modalVisible" ref="el">
    <div class="modal fade" tabindex="-1" style="display: block; padding-right: 15px;" @click.self="clickBackdrop">
      <div class="modal-dialog modal-dialog-centered" :class="[size ? `modal-${size}` : '']">
        <div class="modal-content">
          <div class="modal-header" v-if="!hideHeader">
            <h5 class="modal-title">{{ title }}</h5>
            <button type="button" class="close" v-if="!hideHeaderClose" @click="close">
              <span aria-hidden="true">×</span>
            </button>
          </div>
          <div class="modal-body"><slot/></div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade"></div>
  </div>
</template>

<style lang="less">
@import '@/less/proj';
.modal-outer { .abs; .z(1040); }
</style>