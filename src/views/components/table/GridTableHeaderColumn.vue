<script setup lang="ts">
import { inject } from 'vue';

defineProps({
  label: { type: String, required: true },
  tooltip: { type: String, default: '' },
  sortable: { type: Boolean, default: false },
  focused: { type: Boolean, default: false },
  width: { type: String },
});
const sortState = inject<SortState>('table-sort-state')!;
defineEmits(['sort']);
</script>
<template>
  <th grid-table-header-column :class="{ focused, sortable, [sortState.type]: focused, tooltip }" @click="$emit('sort')" :style="{ width }">
    <span>{{ label }}
      <i v-if="sortable"></i>
    </span>
  </th>
</template>
<style lang="less">
@import "@/less/proj";
@sort-arrow-color: #BFBFCE;
@sort-arrow-focus: #5D5D5D;
@sort-arrow-width: 8;
@sort-arrow-height: 4;
@sort-arrow-space: 2;
@sort-arrow-left: 3;

[grid-table-header-column] {
  span { .flex; .items-center; .gap(4); }
  &.sortable { .pointer;
    i { .ib; .vam; .ml(@sort-arrow-left); .h((@sort-arrow-height * 2 + @sort-arrow-space)); .rel;
      &:before, &:after { .cnt; .abs; .block; .-hc(transparent); .-solid; .-w(0, (@sort-arrow-width / 2)); }
      &:before { .lb; .-tc(@sort-arrow-color); .-tw(@sort-arrow-height); }
      &:after { .lt; .-bc(@sort-arrow-color); .-bw(@sort-arrow-height); }
    }
    &.desc i:before { .-tc(@sort-arrow-focus); }
    &.desc i:after { .-tc(@sort-arrow-focus); }
    &.asc i:before { .-bc(@sort-arrow-focus); }
    &.asc i:after { .-bc(@sort-arrow-focus); }
  }
}
</style>
