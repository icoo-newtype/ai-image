import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import 'simple-line-icons';
import { toastPlugin, useToast } from '@/plugins/toast';
import { modalPlugin } from '@/plugins/modal';
import oax from '@/utils/oax';
import { createPinia } from 'pinia';
import { useAuthInfo, UserInfo } from '@/store/auth';
import { getCookie } from '@/utils';
import 'bootstrap-icons/font/bootstrap-icons.css';
import { useBrowserStore } from '@/store/browser.store';
import { createHead } from '@vueuse/head';

const app = createApp(App)
  .use(createPinia());

const head = createHead();

const init = () => {
  const browserStore = useBrowserStore();
  browserStore.init();

  app
    .use(router)
    .use(head)
    .use(modalPlugin)
    .use(toastPlugin, { type: 'success', duration: 2000 }).mount('#app');

  const error = useToast({ type: 'error', duration: 2000 });
  app.config.errorHandler = (err: any) => {
    if (err.response?.data?.message) error(err.response?.data?.message);
    else {
      if (err.message) error(err.message);
      console.error(err);
    }
  };
};

const authInfo = useAuthInfo();
const token = getCookie('authToken');
if (token) oax.setAuthToken(token);
oax.get<UserInfo>('/api/auth/info').then(({ data }) => {
  authInfo.user = data;
  init();
}).catch(init);