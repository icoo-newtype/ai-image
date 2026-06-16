import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { ObjectParam } from '@/types/components';

export const useStoredParam = (defaultValue?: ObjectParam) => {
  const route = useRoute();
  const key = `param@${route.path}`;
  const fallback = defaultValue ? JSON.stringify(defaultValue) : '{}';
  const param = ref<ObjectParam>(JSON.parse(sessionStorage.getItem(key) ?? fallback));
  watch(param, v => sessionStorage.setItem(key, JSON.stringify(v)), { deep: true });
  Object.keys(sessionStorage).forEach((e) => key !== e && sessionStorage.removeItem(e));
  return param;
};