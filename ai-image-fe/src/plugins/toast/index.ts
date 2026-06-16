import type { SetRequired } from 'type-fest';
import type { Plugin } from 'vue';
import { h, render } from 'vue';
import ToastContainer from './components/ToastContainer.vue';
import { Toast } from './toast';
import type { ToastBaseOptions, ToastInstance, ToastOptions } from './types';

const createEl = (): HTMLElement => {
  if (typeof document === 'undefined') throw new TypeError('document does not exist');

  const $el = document.createElement('div');
  $el.id = 'toast';
  document.body.append($el);

  return $el;
};

export const useToast = (options?: ToastBaseOptions) => {
  const toast = new Toast();
  return (message: string) => toast.add(message, options);
};
useToast.default = ({ type = 'warning', duration = 3000, ...options }: ToastOptions) => {
  const toast = new Toast().defaultOptions(options);
  return (message: string) => toast.add(message, { type, duration });
};

export const toastPlugin: Plugin<SetRequired<ToastOptions, 'type' | 'duration'>> = {
  install(app, options) {
    const $el = createEl();
    const toastApp = h(ToastContainer);
    toastApp.appContext = app._context;
    render(toastApp, $el);

    new Toast().set(toastApp.component?.exposed as ToastInstance).defaultOptions(options);
  },
};