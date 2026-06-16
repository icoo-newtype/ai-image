import { onBeforeUnmount, onMounted } from 'vue';
export const useWindowResize = (handler: () => void) => {
  onMounted(() => handler());
  window.addEventListener('resize', handler);
  onBeforeUnmount(() => {
    window.removeEventListener('resize', handler);
  });
};