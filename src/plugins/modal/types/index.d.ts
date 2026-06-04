import type { Merge } from 'type-fest';
import type { Component } from 'vue';

export type ModalOptions<P = Record<string, string>> = {
  name: string;
  component: Component;
  props?: P;
}

type PromiseFn = <T>(value: T) => void;

export type ModalItem = Merge<ModalOptions, {
  resolve: PromiseFn;
  reject: PromiseFn;
}>

export type ModalInstance = Merge<Component, { addModal<T, P>(options: ModalOptions<P>): Promise<T>; closeModal(name: string): void; }>;
