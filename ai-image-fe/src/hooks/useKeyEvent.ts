import { onBeforeUnmount } from 'vue';

export const useKeyEvent = (handler: (e: KeyboardEvent) => void, upHandler?: (e: KeyboardEvent) => void)=> {
  onBeforeUnmount(() => {
    document.removeEventListener('keydown', handler);
    if (upHandler) document.removeEventListener('keyup', upHandler);
  });
  document.addEventListener('keydown', handler);
  if (upHandler) document.addEventListener('keyup', upHandler);
};