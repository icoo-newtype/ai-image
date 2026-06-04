<script setup lang="ts">
import isEmpty from 'lodash-es/isEmpty';
import { ref } from 'vue';
import type { ToastBaseOptions, ToastItem } from '@/plugins/toast/types';

const id = ref(0);
const toasts = ref<Required<ToastItem>[]>([]);
const intervalId = ref(-1);

const tick = () => {
  toasts.value.forEach(t => {
    t.duration -= 100;
    t.in = true;

    if (t.duration < 100) t.out = true;
  });
  toasts.value = toasts.value.filter(t => t.duration > 0);
  if (!isEmpty(toasts.value)) return;

  clearInterval(intervalId.value);
  intervalId.value = -1;
};
const addToast = (message: string, { type, duration }: Required<ToastBaseOptions>) => {
  const _id = id.value++;
  if (toasts.value[toasts.value.length - 1]?.message === message) {
    toasts.value.pop();
  }
  const toast = { id: _id, message, type, duration, out: false, in: false };
  toasts.value.push(toast);

  if (intervalId.value > -1) return;
  intervalId.value = setInterval(tick, 100) as unknown as number;
};

const removeToast = (id: number) => {
  toasts.value = toasts.value.filter(t => t.id !== id);
};

defineExpose({
  addToast,
});
</script>
<template>
  <ul toast-container>
    <li v-for="toast in toasts" :key="`${toast.id}-${toast.type}`" :class="[toast.type, toast.in && 'in', toast.out && 'out']" class="popping-up" @click="removeToast(toast.id)">
      <span v-html="toast.message" />
    </li>
  </ul>
</template>
<style lang="less">
@import '@/less/proj';
[toast-container] { .fix; .flex; .flex-column; .z(1050); .wf; .events-none;
  > li { .rel; .inline-flex; .tr-to(0.4s); .o(0); .t-y(-40%);
    > span { .flex(1); }
    &.in { .t-y(0); .o(1); }
    &.out { .o(0); }
    &.success { .bgc(#111); .c(#fff); }
    &.error { .bgc(@c-red); .c(#fff); }
    &.warning { .bgc(@c-red); .c(#fff); }
  }
}
[toast-container] { .gap(8); .pv(40); .rt; .items-center;
  > li { .fs(14);  .gap(4); .p(12,20); .br(4); }
}
</style>
