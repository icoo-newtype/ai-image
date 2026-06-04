import type { Component, Plugin } from 'vue';
import { h, render } from 'vue';
import ModalContainer from './components/ModalContainer.vue';
import { Modal } from './modal';
import type { ModalInstance } from './types';

const createEl = (): HTMLElement => {
  if (typeof document === 'undefined') throw new TypeError('document does not exist');

  const $el = document.createElement('div');
  $el.id = 'modal';
  document.body.append($el);

  return $el;
};

export const useModal = <T, P>(component: Component) => new Modal().add<T, P>(component);

export const modalPlugin: Plugin = {
  install(app) {
    const $el = createEl();
    const modalApp = h(ModalContainer);
    modalApp.appContext = app._context;
    render(modalApp, $el);

    new Modal().set(modalApp.component?.exposed as ModalInstance);
  },
};