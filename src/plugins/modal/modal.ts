import uniqueId from 'lodash-es/uniqueId';
import type { Component } from 'vue';
import type { ModalInstance } from './types';

export class Modal {
  static #instance: Modal;
  #modal: ModalInstance;

  constructor() {
    if (Modal.#instance) return Modal.#instance;
    Modal.#instance = this;
  }

  set(modal: ModalInstance): Modal {
    this.#modal = modal;
    return this;
  }

  add<T, P>(component: Component): (props?: P) => Promise<T> {
    const name = uniqueId('modal-');
    return props => this.#modal.addModal<T, P>({ name, component, props });
  }

  close(name: string): void {
    this.#modal.closeModal(name);
  }
}
