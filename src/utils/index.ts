import { forEach, isString } from 'lodash-es';
import { onActivated, onDeactivated, onMounted, onUnmounted } from 'vue';

export const setCookie = (name: string, value: string) => {
  const expire = new Date();
  expire.setFullYear(expire.getFullYear() + 1);
  document.cookie = name + '=' + encodeURIComponent(value) + '; path=/; expires=' + expire.toUTCString();
};

export const removeCookie = (name: string) => {
  document.cookie = name + '=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT;';
};

export const getCookie = (name: string) => {
  let value = null;
  if (document.cookie) {
    const array = document.cookie.split((name + '='));
    if (array.length >= 2) {
      const arraySub = array[1].split(';');
      value = decodeURIComponent(arraySub[0]);
    }
  }
  return value;
};

export const addClass = (el: HTMLElement | string, c: string) => {
  const add = (e: HTMLElement) => {
    if (e.classList.contains(c)) return;
    e.classList.add(c);
  };
  if (typeof el === 'string') {
    forEach(document.querySelectorAll(el) as NodeListOf<HTMLElement>, add);
  } else {
    add(el);
  }
};

export const formatBytes = (byte: number, decimals = 2) => {
  if (byte === 0) return '0 Bytes';
  const c = 1024;
  const e = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const f = Math.floor(Math.log(byte) / Math.log(c));
  return `${parseFloat((byte / (c ** f)).toFixed(decimals))} ${e[f]}`;
};

export const asyncAll = <T, R>(target: T[], fn: (v: T) => Promise<R>) => {
  return Promise.all(target.map(item => fn(item)));
};

export const imageVal = (img?: string) => {
  if (!img?.includes('#')) return undefined;
  const s = img?.split('#');
  return { location: s[0], sq: s[1] } as FileItem;
};

export const sleep = (dura: number) => new Promise<void>(resolve => setTimeout(resolve, dura));

export const getMediaTypeFromPath = (path: string): 'image' | 'video' | 'audio' | 'resource' | null => {
  const cleanPath = path.split('#')[0];
  const ext = cleanPath.split('.').pop()?.toLowerCase();
  const map: Record<string, 'image' | 'video' | 'audio' | 'resource'> = {
    gif: 'image', jpg: 'image', jpeg: 'image', png: 'image', svg: 'image', webp: 'image',
    mp4: 'video', webm: 'video', avi: 'video', mov: 'video',
    mp3: 'audio', ogg: 'audio', wav: 'audio',
    pdf: 'resource', doc: 'resource', docx: 'resource',
    xls: 'resource', xlsx: 'resource', ppt: 'resource', pptx: 'resource',
    hwp: 'resource', zip: 'resource', txt: 'resource',
  };
  return ext ? (map[ext] ?? null) : null;
};

export const enterToBr = (cont: string): string => {
  if (!cont || !isString(cont)) return '';
  return cont.replace(/[\r\n]/g, '<br>').replace(/(\s{2,})/g, (match, space) => space.replace(/\s/g, '&nbsp;'));
};

export const normalizeString = (str: string) => str?.replace(/\s+/g, ' ').trim();

export const useWindowEvent = (event: string, handler: EventListenerOrEventListenerObject, options?: boolean | AddEventListenerOptions) => {
  let deactivated = false;
  onMounted(() => window.addEventListener(event, handler, options));
  onUnmounted(() => window.removeEventListener(event, handler));
  onActivated(() => {
    if (deactivated) window.addEventListener(event, handler, options);
    deactivated = false;
  });
  onDeactivated(() => {
    deactivated = true;
    window.removeEventListener(event, handler);
  });
};