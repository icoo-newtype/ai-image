<script setup lang="ts">
import Modal from '@/views/components/common/Modal.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import { ref } from 'vue';
import BButton from '@/views/components/common/BButton.vue';
import BForm from '@/views/components/common/BForm.vue';

const props = defineProps<{ msg: string, desc?: string, prompt?: boolean, cancel?: boolean, bool?: boolean, variant?: Variant, placeholder?: string }>();
const emit = defineEmits<{ resolve: [any], reject: [] }>()
const userInput = ref();
const validator = ref();
const open = ref(true);
const result = ref<any>(false);
const reject = () => open.value = false;
const resolve = () => {
  if (props.prompt) {
    if (!validator.value?.validate()) return;
    result.value = userInput.value;
  } else {
    result.value = true;
  }
  open.value = false;
};

const changed = (v: boolean) => {
  if (v) return;
  if (result.value || props.bool) emit('resolve', result.value);
  else emit('reject');
}
</script>

<template>
  <Modal dialog v-model="open" :prevent-close-on-backdrop="bool" :prevent-close-on-esc="bool" hide-header @changed="changed">
    <div class="msg" v-html="msg"></div>
    <div class="desc" v-html="desc" v-if="desc"></div>
    <b-form class="prompt" v-if="prompt" ref="validator">
      <TextInput v-model="userInput" :placeholder="placeholder" required/>
    </b-form>
    <div class="tr mt-40">
      <b-button @click="reject" variant="light" class="mr-2" v-if="cancel">취소</b-button>
      <b-button @click="resolve" :variant="variant ?? 'primary'">확인</b-button>
    </div>
  </Modal>
</template>

<style lang="less">
@import '@/less/proj';
[dialog] {
  .modal-dialog { .w(480); .-box; }
  .modal-content { .-a; .br(3); .shadow;
    .modal-body { .p(30, 36, 36);
      .msg { .fs(18,1.2); .bold; }
      .desc { .mt(8); .fs(14,1.5); .medium; .c(#9E9E9E); }
      .prompt { .mt(30); }
    }
  }
  .cancel {.o(.5);}
}
</style>