<script setup lang="ts">
import { markRaw, ref } from 'vue';
import type { ModalItem, ModalOptions } from '@/plugins/modal/types';

const modals = ref<ModalItem[]>([]);

const addModal = ({ name, component, props }: ModalOptions) => {
  if (!component) throw new TypeError('Component does not exist');

  return new Promise((resolve, reject) => {
    modals.value = [...modals.value, { component: markRaw(component), name, props, resolve, reject }];
  });
};

const closeModal = (name: string): void => {
  modals.value = modals.value.filter(m => m.name !== name);
};

const resolveModal = <T>(value: T, { name, resolve }: ModalItem): void => {
  resolve<T>?.(value);
  closeModal(name);
};

const rejectModal = (value: unknown, { name }: ModalItem): void => {
  closeModal(name);
};

defineExpose({
  addModal,
  closeModal,
});

</script>
<template>
  <component
    :is="modal.component"
    v-for="(modal, i) in modals"
    :key="`${i}-${modal.name}`"
    v-bind="modal.props"
    @resolve="resolveModal($event, modal)"
    @reject="rejectModal($event, modal)"
    @close="closeModal(modal.name)"
  />
</template>