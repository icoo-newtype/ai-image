<script setup lang="ts">
import { computed, inject, PropType } from 'vue';
import GridTableHeaderColumn from '@/views/components/table/GridTableHeaderColumn.vue';
import GridTableDataColumn from '@/views/components/table/GridTableDataColumn.vue';

const props = defineProps({
  label: { type: String, default: '' },
  tooltip: { type: String, default: '' },
  field: { type: [String, Function] as PropType<string | ((row: any) => any)>, default: null },
  preferSort: { type: String as PropType<'asc' | 'desc'>, default: 'asc' },
  width: { type: String, default: '' },
});

const onHeader = inject('on-table-header');
const sortState = inject<SortState>('table-sort-state')!;
const comp: unknown = onHeader ? GridTableHeaderColumn : GridTableDataColumn;
const sortable = computed(() => props.field && sortState.use);
const focused = computed(() => props.field && props.field === sortState.field);
const sort = () => {
  if (!sortable.value) return;
  if (sortState.field === props.field) {
    if (sortState.type === props.preferSort)
      sortState.type = sortState.type === 'asc' ? 'desc' : 'asc';
    else {
      sortState.field = null;
    }
  } else {
    sortState.type = props.preferSort;
    sortState.field = props.field;
  }
};
const w = computed(() => {
  if (props.width && /^\d+$/.test(props.width)) return `${props.width}px`;
  return props.width;
})
</script>
<template>
  <component :is="comp" :label="label" :focused="focused" :sortable="sortable" :tooltip="tooltip" :width="w" @sort="sort">
    <slot/>
  </component>
</template>