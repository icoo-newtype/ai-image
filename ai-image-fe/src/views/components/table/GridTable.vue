<script setup lang="ts" generic="T extends Record<string, any>">
import { computed, provide, reactive, ref, watch } from 'vue';
import { forEach, get, map, orderBy } from 'lodash-es';
import { closest } from '@/utils/domUtil';
import { sleep } from '@/utils';
import ScrollHolder from '@/views/components/ScrollHolder.vue';
import GridTableHeader from '@/views/components/table/GridTableHeader.vue';
import BCheckbox from '@/views/components/common/BCheckbox.vue';
import BPagination from '@/views/components/common/BPagination.vue';

const props = defineProps<{
  idField?: string;
  useOrder?: boolean;
  useSelect?: boolean;
  useSelectAll?: boolean;
  useSingleSelect?: boolean;
  useSort?: boolean;
  list?: T[];
  initSortField?: string;
  initSortType?: 'asc' | 'desc';
  selectedIds?: (string | number)[];
  autoSize?: boolean;
  maxHeight?: number;
  minHeight?: number;
  darkScroll?: boolean;
  innerScroll?: boolean;
  trClass?: (row: T) => string;
  focus?: string | number;
  useClick?: boolean;
  paging?: { pageNo: number, perPage: number, listCount: number };
}>();
const emit = defineEmits(['update:selectedIds', 'reorder', 'update:focus', 'detail', 'click', 'bottom', 'changePage']);
const order = ref<T[]>();
const moving = ref(false);
const focusedId = ref<(string | number)>();
const selectedValues = ref<(string | number)[]>([]);
const sortState = reactive({
  use: props.useSort,
  type: props.initSortType,
  field: props.initSortField,
} as { use: boolean, type: 'asc' | 'desc', field?: string | ((row: any) => any) });

watch(() => props.useSort, v => sortState.use = v);
watch(() => props.initSortField, () => {
  sortState.field = props.initSortField;
  sortState.type = props.initSortType ?? 'asc';
});
watch(() => props.list, async () => {
  selectedValues.value = [];
  focusedId.value = undefined;
  if (props.useOrder) order.value = [...(props.list ?? [])];
  await sleep(100);
}, { deep: true });
watch(() => props.selectedIds, v => {
  if (selectedValues.value === v) return;
  selectedValues.value = v ?? [];
});
watch(() => props.focus, () => {
  focusedId.value = props.focus;
});
watch(focusedId, () => {
  emit('update:focus', focusedId.value);
});
watch(selectedValues, () => {
  emit('update:selectedIds', selectedValues.value);
});

provide('table-sort-state', sortState);
provide('on-table-header', false);

const sorted = computed(() =>
    (sortState.use && sortState.field)
        ? orderBy(props.list, [row => typeof sortState.field === 'function' ? sortState.field(row) : get(row, sortState.field!) || null], [sortState.type])
        : props.list,
);

const items = computed(() => {
  return order.value || sorted.value;
});

const selectBehavior = computed(() => props.useSelect || props.useSingleSelect);
const selectAll = computed({
  get() {
    if (!props.list || !props.list.length || !selectedValues.value) return false;
    return props.list.every((row, idx) => selectedValues.value.includes(props.idField ? row[props.idField] : idx));
  },
  set(v) {
    selectedValues.value = v && props.list ? props.list.map((row, idx) => props.idField ? row[props.idField] : idx) : [];
  },
});
const startOrder = () => {
  sortState.field = undefined;
  if (!props.list) return;
  order.value = [...props.list];
};
const orderDrag = (idx: number, e: MouseEvent) => {
  const target = closest(e.target as HTMLElement, 'tr') as & HTMLElement & { y?: number };
  if (!target || !target.parentNode) return;
  const parent = target.parentNode as HTMLElement;
  const scrollArea = parent.closest('.scroll-area') as HTMLElement;
  const siblings = parent.children as HTMLCollectionOf<HTMLElement>;
  const heights = map(siblings, el => el.offsetHeight);
  const ordered = map(siblings, (_, idx) => idx);
  const max = parent.offsetHeight;
  let position = idx;

  const sum = (i: number) => i > 0 ? ordered.slice(0, i).map(x => heights[x]).reduce((a, v) => a + v) : 0;
  const transTo = (el: HTMLElement & { y?: number }, y: number) => {
    const b = max - el.offsetHeight;
    const top = y < 0 ? 0 : y > b ? b : y;
    const delta = top - el.offsetTop;
    el.y = top;
    el.style.transform = `translateY(${delta}px)`;
  };

  const align = (drag: HTMLElement | null = null) => {
    let o = 0;
    ordered.forEach(idx => {
      if (siblings[idx] !== drag) transTo(siblings[idx], o);
      o += heights[idx];
    });
  };

  moving.value = true;
  target.classList.add('drag');
  const down = e.screenY;
  const startY = target.offsetTop;
  const maxScroll = scrollArea.scrollHeight - scrollArea.offsetHeight;
  let scrollMove = 0;
  const scrollInterval = setInterval(() => {
    if (scrollMove === 0) return;
    if (scrollMove < 0 && scrollArea.scrollTop <= 0) return;
    if (scrollMove > 0 && scrollArea.scrollTop >= maxScroll) return;
    scrollArea.scrollTo(0, scrollArea.scrollTop + scrollMove);
    transTo(target, (target.y ?? 0) + (scrollMove * 2));
  }, 40);
  const move = (e: MouseEvent) => {
    e.preventDefault();
    const top = startY + e.screenY - down;
    const bottom = top + target.offsetHeight;
    transTo(target, top);
    if (top - scrollArea.scrollTop < 0) scrollMove = -(target.offsetHeight * 0.5);
    else if (bottom - scrollArea.scrollTop > scrollArea.offsetHeight) scrollMove = target.offsetHeight * 0.5;
    else scrollMove = 0;
    const points = ordered.map((idx, i) => heights[idx] * 0.5 + sum(i));
    const d = points.filter((v, i) => v > top && i < position).length;
    if (d > 0) {
      ordered.splice(position - d, 0, ...ordered.splice(position, 1));
      position -= d;
      align(target);
      return;
    }
    const u = points.filter((v, i) => v < bottom && i > position).length;
    if (u > 0) {
      ordered.splice(position + u, 0, ...ordered.splice(position, 1));
      position += u;
      align(target);
    }
  };
  const up = () => {
    clearInterval(scrollInterval);
    target.classList.remove('drag');
    align();
    document.removeEventListener('mousemove', move);
    document.removeEventListener('mouseup', up);
    setTimeout(() => {
      moving.value = false;
      forEach(siblings, e => e.style.transform = '');
      order.value = ordered.map(idx => order.value![idx]);
      emit('reorder', order.value?.map((item, idx) => props.idField ? item[props.idField] : idx));
    }, 300);
  };
  document.addEventListener('mousemove', move);
  document.addEventListener('mouseup', up);
};
const rowClick = (row: T, index: number, e: MouseEvent) => {
  const tagName = (e.target as HTMLElement).tagName;
  if (tagName !== 'TH' && tagName !== 'TD') return;
  const v = props.idField ? row[props.idField] : index;
  if (props.useClick) {
    emit('click', v);
  } else if (props.useSelect) {
    selectedValues.value.includes(v) ? selectedValues.value = selectedValues.value.filter(x => x !== v) : selectedValues.value.push(v);
  } else if (props.useSingleSelect) {
    selectedValues.value.includes(v) ? selectedValues.value = [] : selectedValues.value = [v];
  } else {
    focusedId.value = v;
  }
};
const rowDblClick = (row: T, index: number) => {
  emit('detail', row, index);
};
if (props.useOrder) startOrder();

const scrollHolder = ref();
defineExpose({
  isBottom: () => scrollHolder.value.isBottom()
});

const changePage = (v: number) => {
  emit('changePage', v);
}
</script>
<template>
  <div grid-table>
    <table :class="{ auto: autoSize }">
      <thead>
      <grid-table-header>
        <th v-if="useOrder" class="order-column">
          <slot name="useOrderTitle"></slot>
        </th>
        <th v-if="selectBehavior" class="select-column">
            <span v-if="useSelectAll">
              <b-checkbox v-model="selectAll"/>
            </span>
          <template v-else>선택</template>
        </th>
        <slot :get="{ by: () => null }" :row="{} as T"/>
      </grid-table-header>
      </thead>
    </table>
    <scroll-holder ref="scrollHolder" :stop="moving" :max-height="maxHeight ? maxHeight - 40 : 0" :min-height="maxHeight ? maxHeight - 40 : 0" :dark-thumb="darkScroll" :inner-thumb="innerScroll"
                   @bottom="emit('bottom')">
      <table :class="{ auto: autoSize }" v-if="items?.length">
        <tbody :class="{ moving }">
        <tr v-for="(row, index) in items as T[]" :key="idField ? row[idField] : index"
            :class="[{ focused: focusedId !== undefined && (idField ? row[idField] : index).toString() === focusedId.toString(), selected: idField && selectedValues.includes(row[idField]) }, trClass?.(row)]"
            @dblclick="rowDblClick(row, index)"
            @mousedown="e => rowClick(row, index, e)">
          <td v-if="useOrder" class="order-column">
            <i class="icon-menu" @mousedown.stop="orderDrag(index, $event)"></i>
          </td>
          <td v-if="selectBehavior" class="select-column">
            <b-checkbox blank type="square" v-model="selectedValues" :value="idField ? row[idField] : index"/>
          </td>
          <slot :index="index" :no="paging ? paging.listCount - (paging.pageNo - 1) * paging.perPage - index : items.length - index" :row="row" :get="{ by: (path: string) => get(row, path) }"/>
        </tr>
        </tbody>
      </table>
      <div v-else-if="$slots.empty"><slot name="empty"/></div>
    </scroll-holder>
    <nav class="footer d-flex justify-content-between mt-80">
      <div>
        <slot name="footer-left"/>
      </div>
      <template v-if="paging && items?.length">
        <b-pagination :paging="paging" class="mb-0" @changePage="changePage" />
      </template>
      <div v-else>
        <slot name="footer-center"/>
      </div>
      <div>
        <slot name="footer-right"/>
      </div>
    </nav>
  </div>
</template>