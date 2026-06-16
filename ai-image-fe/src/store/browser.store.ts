import { defineStore } from 'pinia';
import { ref } from 'vue';
import { Media, MEDIAS } from '@/types/media';
const ScreenWidth: Record<Media, number> = {
  MS: 0,
  MM: 360,
  ML: 420,
  TP: 768,
  TL: 1024,
  DS: 1440,
  DM: 1600,
  DL: 1920,
}
const Screens = MEDIAS.slice().reverse();

export type BrowserStore = ReturnType<typeof useBrowserStore>
export const useBrowserStore = defineStore('browserStore', () => {
  const scrollValue = ref<number>(0);
  const scrollDirection = ref<'up' | 'down'>('up');
  const scrolled = ref(false);
  const touch = ref(false);
  const matchedMedia = ref<Media>('DS');
  const theme = ref('');
  const frozen = ref(false);
  const checkMatchedMedia = () => {
    for (const screen of Screens) {
      if (window.matchMedia(`(min-width: ${ScreenWidth[screen]}px)`).matches) {
        matchedMedia.value = screen;
        return;
      }
    }
  }
  let initialized = false;
  const init = () => {
    if (initialized) return;
    initialized = true;
    touch.value = 'ontouchstart' in window;
    document.body.classList.add(touch.value ? 'touch' : 'no-touch');
    window.addEventListener('scroll', () => {
      if (frozen.value) return;
      const v = window.scrollY;
      scrollDirection.value = v > scrollValue.value ? 'down' : 'up';
      scrolled.value = v > 0;
      scrollValue.value = v;
    });
    window.addEventListener('resize', checkMatchedMedia);
    checkMatchedMedia();
  }

  return { scrollValue, scrollDirection, scrolled, matchedMedia, touch, theme, init, frozen };
});