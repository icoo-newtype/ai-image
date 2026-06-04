import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useModalStack = defineStore('modalDepth', () => {
  const sq = ref(0);
  const stack = ref<number[]>([]);
  const open = () => {
    sq.value += 1;
    stack.value.push(sq.value);
    return sq.value;
  };
  const close = (key: number) => stack.value = stack.value.filter(k => k !== key);
  const check = (key: number) => stack.value[stack.value.length - 1] === key;
  return {
    stack,
    open,
    close,
    check,
  };
});