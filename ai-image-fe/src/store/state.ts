import { defineStore } from 'pinia';
import { computed, ref, watch } from 'vue';
import { getCookie, setCookie } from '@/utils';

export const useState = defineStore('stateInfo', () => {
  const lang = ref<string>(getCookie('admin-lang') as string ?? 'ko');
  const langList: Record<string, string> = { 'ko': '한국어', 'en': '영어', 'zh': '중국어' };
  const language = computed(() => {
    return langList[lang.value] ?? '';
  });
  const afterLogin = ref<string>();
  watch(lang, v => {
    setCookie('admin-lang', v);
    location.href = '/admin/';
  });
  const host = computed(() => import.meta.env.VITE_APP_HOST);
  return { lang, langList, language, afterLogin, host };
});