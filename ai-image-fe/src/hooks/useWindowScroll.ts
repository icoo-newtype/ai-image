import { onBeforeUnmount, onMounted } from 'vue';
export const useWindowScroll = (handler: () => void) => {
  onMounted(() => handler());
  window.addEventListener('scroll', handler);
  onBeforeUnmount(() => {
    window.removeEventListener('scroll', handler);
  });
};