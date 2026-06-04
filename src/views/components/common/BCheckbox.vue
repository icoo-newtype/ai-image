<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps({
  disabled: { type: Boolean, default: false },
  value: {},
  modelValue: {},
  label: String,
});
const emit = defineEmits(['update:modelValue']);
const model = computed({
  get: () => props.modelValue,
  set: v => emit('update:modelValue', v)
});
</script>
<template>
  <label b-checkbox class="custom-control custom-checkbox">
    <input type="checkbox" class="custom-control-input" :value="value" v-model="model" :disabled="disabled">
    <span class="custom-control-label">{{ label }}</span>
  </label>
</template>
<style lang="less">
@import "@/less/proj";
[b-checkbox] { .mb(0);
  .custom-control-label { .pointer; }
  .custom-control-label:before, .custom-control-label:after { .t(1); }
}
</style>