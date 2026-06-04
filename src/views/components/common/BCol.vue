<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{ lg?: string, cols?: string | number }>();
const cls = computed(() => {
  if (props.lg) return `col-lg-${props.lg}`;
  if (props.cols) return `col-${props.cols}`;
  return '';
})
</script>

<template>
  <div :class="cls"><slot /></div>
</template>

<style lang="less">
@import '@/less/proj';
</style>