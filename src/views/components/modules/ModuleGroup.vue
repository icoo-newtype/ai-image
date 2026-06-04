<script setup lang="ts">

import { inject, ref, watchEffect } from 'vue';
import Module from '@/views/components/modules/Module.vue';
import Drag from '@/views/components/article/Drag.vue';
import { ModuleItem } from '@/types/components';
import { cloneDeep } from 'lodash-es';
import { useModal } from '@/plugins/modal';
import ModuleModal from '@/views/components/modal/ModuleModal.vue';
import BButton from '@/views/components/common/BButton.vue';

const props = defineProps(['modelValue']);
const emit = defineEmits(['update:modelValue']);

const modules = ref<ModuleItem[]>();
const dragging = ref(false);
const keyIndex = ref(0);

const insertModule = (idx: number) => {
  openModal(idx);
  emit('update:modelValue', modules.value);
  keyIndex.value++;
};
const removeModule = (idx: number) => {
  modules.value?.splice(idx, 1);
  emit('update:modelValue', modules.value);
};
const copyModule = (idx: number) => {
  modules.value?.splice(idx, 0, { ...cloneDeep(modules.value[idx]) });
  emit('update:modelValue', modules.value);
  keyIndex.value++;
};

const up = (idx: number) => {
  if (idx <= 0 || !modules.value?.length) return;
  const arr = [...modules.value];
  [arr[idx], arr[idx - 1]] = [arr[idx - 1], arr[idx]];
  modules.value = arr;
  emit('update:modelValue', modules.value);
  keyIndex.value++;
};

const down = (idx: number) => {
  if (!modules.value?.length || idx >= modules.value?.length - 1) return;
  const arr = [...modules.value];
  [arr[idx], arr[idx + 1]] = [arr[idx + 1], arr[idx]];
  modules.value = arr;
  emit('update:modelValue', modules.value);
  keyIndex.value++;
};
const onDragStart = () => {
  dragging.value = true;
};
const onDragEnd = () => {
  dragging.value = false;
  emit('update:modelValue', modules.value);
  keyIndex.value++;
};

watchEffect(() => modules.value = props.modelValue || []);

const modal = useModal<ModuleItem, {}>(ModuleModal);
const openModal = async (idx: number) => {
  const moduleData = await modal();
  modules.value?.splice(idx, 0, moduleData);
};
</script>
<template>
  <div class="modules" v-if="modules" :class="[{ dragging }]" :key="keyIndex">
    <Drag :class="{ dragging: false }" class="module-wrap" handle=".handle" v-model="modules" @start="onDragStart" @end="onDragEnd">
      <transition-group name="fade">
        <template v-for="(el, idx) in modules" :key="`module${idx}`">
          <Module v-model="modules[idx]" :idx="idx" @copy="copyModule(+idx)" @remove="removeModule(+idx)" @insertBefore="insertModule(+idx)" @insertAfter="insertModule(+idx+1)" @up="up(+idx)" @down="down(+idx)"/>
        </template>
      </transition-group>
    </Drag>
    <div class="f-add mt-60" v-if="!modules.length">
      <b-button class="px-4" variant="primary" @click="insertModule(0)">ADD MODULE</b-button>
    </div>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

.fade-enter-active,
.fade-leave-active {
  transition: all 0.5s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20%);
}
</style>