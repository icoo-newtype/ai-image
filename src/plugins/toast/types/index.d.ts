import type { Merge } from 'type-fest';

export type ToastType = 'error' | 'warning' | 'success';

export type ToastBaseOptions = {
  type?: ToastType;
  duration?: number;
}

export type ToastOptions = ToastBaseOptions;

export type ToastItem = Merge<ToastBaseOptions, {
  id: number;
  message: string;
  in: boolean;
  out: boolean;
}>

export type ToastInstance = { addToast(message: string, options?: ToastOptions): void; };
