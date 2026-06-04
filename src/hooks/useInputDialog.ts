import { useModal } from '@/plugins/modal';
import Dialog from '@/views/components/common/Dialog.vue';

export const useInputDialog = () => {
  const openDialog = useModal<string, { msg: string, desc?: string, placeholder?: string, prompt?: boolean, cancel?: boolean, bool?: boolean }>(Dialog);
  return (msg: string, desc?: string, placeholder?: string) => openDialog({ msg, desc, placeholder, prompt: true });
}

export const useConfirm = () => {
  const openDialog = useModal<void, { msg: string, desc?: string, prompt?: string, cancel?: boolean, bool?: boolean }>(Dialog);
  return (msg: string, desc?: string) => openDialog({ msg, desc });
}

export const useBoolConfirm = () => {
  const openDialog = useModal<boolean, { msg: string, desc?: string, placeholder?: string, prompt?: boolean, cancel?: boolean, bool?: boolean }>(Dialog);
  return (msg: string, desc?: string) => openDialog({ msg, desc, bool: true });
}


export const useConfirmCancel = () => {
  const openDialog = useModal<void, { msg: string, desc?: string, prompt?: string, cancel?: boolean, bool?: boolean }>(Dialog);
  return (msg: string, desc?: string) => openDialog({ msg, desc, cancel: true });
}