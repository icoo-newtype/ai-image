import type { ToastBaseOptions, ToastInstance, ToastOptions } from './types';

export class Toast {
  static #instance: Toast;
  #toastDefaultOptions: ToastBaseOptions;
  #toast: ToastInstance;

  constructor() {
    if (Toast.#instance) return Toast.#instance;
    Toast.#instance = this;
  }

  defaultOptions({ type, duration }: ToastOptions): Toast {
    if ([type, duration].some(is => is)) this.#toastDefaultOptions = { type, duration };
    return this;
  }

  set(Toast: ToastInstance): Toast {
    this.#toast = Toast;
    return this;
  }

  add(message: string, options?: ToastBaseOptions): void {
    return this.#toast.addToast(message, { ...this.#toastDefaultOptions, ...options });
  }
}