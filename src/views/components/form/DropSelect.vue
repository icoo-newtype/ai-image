<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { SelectComponentEmits, SelectComponentProps, SelectItem } from '@/types/components';
import { getLabel, getValue } from '@/utils/componentUtil';
import { forEach } from 'lodash-es';

const props = defineProps<SelectComponentProps>();
const emit = defineEmits<SelectComponentEmits>();
const fallback = computed(() => ({ value: '', label: props.placeholder ?? '' }));
const opened = ref(false);
const open = ref(false);
const selected = ref<SelectItem | null>(null);
const invalid = computed(() => {
  return props.required && (selected.value === null || (typeof selected.value === 'object' && !selected.value.value && selected.value.value !== 0) || !selected.value);
});
const dropHeight = ref('0');
const dropWidth = ref('0');
const el = ref<HTMLElement>();
const scrollBody = ref<HTMLElement>();

const close = () => {
  open.value = false;
  dropHeight.value = '0';
  window.removeEventListener('mousedown', closeCheck);
};

const closeCheck = (event: MouseEvent) => {
  if (el.value?.contains(event.target as HTMLElement)) return;
  close();
};

const openSelect = () => {
  if (!scrollBody.value) return;
  open.value = true;
  opened.value = true;
  const selectedItem = scrollBody.value.querySelector<HTMLElement>('li.on');
  if (selectedItem) {
    scrollBody.value.scrollTop = selectedItem.offsetTop;
  }
  dropHeight.value = `${scrollBody.value.offsetHeight}px`;
  dropWidth.value = `${scrollBody.value.offsetWidth}px`;
  window.addEventListener('mousedown', closeCheck);
};

const computedList = computed(() => {
  return props.split ? props.split.split(/\s*,\s*/) : props.list;
});

const toggle = () => {
  if (!open.value) {
    openSelect();
  } else {
    close();
  }
};

const animated = () => {
  opened.value = open.value;
};

const change = (val: SelectItem | null, artificial = false) => {
  if (selected.value && getValue(selected.value) === getValue(val)) return;
  selected.value = val;
  const v = val && getValue(val);
  emit('update:modelValue', v);
  if (artificial) emit('change', v);
};

const getDepth = (str: string | number | null) => {
  if (str === null) return '';
  if (typeof str === 'number') return '';
  return 'dp-' + str.split('-').length;
};

const update = () => {
  let val = null;
  if (computedList.value) {
    val = computedList.value.find(item => getValue(item) === props.modelValue) ?? null;
  } else if (props.obj && props.modelValue) {
    val = props.obj[props.modelValue] ? { label: props.obj[props.modelValue], value: props.modelValue } : null;
  }
  if (!val) {
    if (props.placeholder) val = fallback.value;
    else if (computedList.value) [val] = computedList.value;
    else if (props.obj) forEach(props.obj, (label, value) => {
      val = { label, value };
      return false;
    });
  }
  change(val);
};

update();
watch(() => [props.list, props.obj, props.split], update);
</script>
<template>
  <div drop-select :class="{ opened, open, invalid, readonly }" @click="toggle" ref="el">
    <button class="btn" :class="variant && `btn-${variant}`" v-html="selected && ( typeof selected === 'object' ? selected.label : selected )"></button>
    <div drop-select-wrapper :style="{ height: dropHeight }" @transitionend="animated">
      <div class="scroll-body" :class="variant" ref="scrollBody">
        <ul>
          <li ontouchstart="" class="item" :class="{on: modelValue === ''}" @click="change(fallback, true)" v-if="placeholder && !required">{{ placeholder }}</li>
          <li ontouchstart="" class="item" :class="[
            getValue(item) === modelValue ? 'on' : '',
            variant === 'category' ? getDepth(getValue(item)) : '',
          ]" v-for="item in computedList" :key="getValue(item)+getLabel(item)" @click="change(item, true)" v-html="getLabel(item)"/>
          <li ontouchstart="" class="item" :class="{on: modelValue === key}" v-for="(label, key) in obj" :key="key + label" @click="change({ label, value: key }, true)" v-html="label"/>
        </ul>
      </div>
    </div>
  </div>
</template>

<style lang="less">
@import '@/less/proj';
.input-group > .input-group-prepend > [drop-select] > .btn { .br-r(0); .-r(@c-border); }
.input-group > .input-group-append > [drop-select] > .btn { .br-l(0); .-l(@c-border); }

[drop-select] { .pointer; .rel;
  button { .wf; .tl; .pl(13); .pr(24); background-color: #f4f4f4; color: #111;
    &:after { .cnt; .block; .abs; .rt(0.6rem, 50%); .t-y(-50%); .arrow-down(0.3em); }
    &:hover {.bgc(#e0e0e0);}
    &:focus {.bgc(#f4f4f4);}
  }
  [drop-select-wrapper] { .bgc(#fff); .-box; .crop; .h(0); .abs; .lt(0, 100%); .mt(3); .z(100); .-box; .br(0.25rem); .o(0); .wf;
    transition: opacity 0.1s 0.2s, height 0.3s;
    box-shadow: 0 5px 13px 0 rgba(0, 0, 0, 0.15);
    .scroll-body { .max-h(280); .lb; .abs; min-width: 100%; overflow: auto; }
    ul { .mb(0); }
    li { .p(8, 16);
      &:hover {.bgc(#f7f7f7);}
      &:active {color: #111;}
      &:first-child { .pt(10); }
      &:last-child { .pb(10); }
      &.on { .bgc(#f7f7f7); }
    }
    .primary {
      li:active { color: #fff; background-color: #20a8d8; border-color: #20a8d8; }
      li.on { color: #fff; background-color: #1985ac; border-color: #187da0; }
    }
    .outline-info {
      li:active { color: #fff; background-color: #63c2de; border-color: #63c2de; }
      li.on { color: #fff; background-color: #63c2de; border-color: #63c2de; }
    }
  }
  &.open {
    [drop-select-wrapper] { opacity: 1; transition: opacity 0.1s, height 0.3s; }
  }
  &.readonly {
    label { .bgc(#eee); }
  }
  &.opened {
    [drop-select-wrapper] { box-shadow: 0 2px 13px -1px rgba(0, 0, 0, 0.12); }
  }
  .validated &.invalid {
    button { .border-danger; }
  }
}
</style>