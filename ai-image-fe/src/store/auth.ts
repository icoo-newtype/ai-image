import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export type UserInfo = { id: string, name: string, roles: string };

export const useAuthInfo = defineStore('authInfo', () => {
  const user = ref<UserInfo>();
  const isAuthenticated = computed(() => !!user.value); // 로그인 여부 추상화
  return { user, isAuthenticated };
});

export const useLoginPageStore = defineStore('LoginPageStore', () => {
  const authInfo = useAuthInfo();
  const loggedInSlugs = ref<string[]>(
    JSON.parse(sessionStorage.getItem('loggedInSlugs') ?? '[]')
  );

  const isLogin = (slug: string): boolean => {
    return authInfo.isAuthenticated || loggedInSlugs.value.includes(slug);
  };
  const setLoginPage = (slug: string) => {
    if (!isLogin(slug)) {
      loggedInSlugs.value.push(slug);
      sessionStorage.setItem('loggedInSlugs', JSON.stringify(loggedInSlugs.value));
    }
  };
  return { isLogin, setLoginPage };
});