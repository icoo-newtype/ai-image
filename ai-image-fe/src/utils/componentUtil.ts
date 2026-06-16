import { SelectItem } from '@/types/components';

export const getValue = (item: SelectItem | null) => {
  if (item === null) return null;
  if (typeof item === 'object') return item.value;
  return item;
}

export const getLabel = (item: SelectItem) => {
  if (typeof item === 'object') return item.label;
  return item;
}
