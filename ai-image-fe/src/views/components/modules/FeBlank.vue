<script setup lang="ts">

defineProps<{
  info: Record<string, any>
}>();

</script>
<template>
  <div fe-blank :class="[info.type, info.value.id]"></div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[fe-blank] {
  &.blank-S { .h(20); }
  &.blank-M { .h(80); }
  &.blank-L { .h(120); }
  &.blank-20 { .h(20); }
  &.blank-40 { .h(40); }
  &.blank-60 { .h(60); }
  &.blank-100 { .h(100); }
  &.blank-120 { .h(120); }
}

@media (@tp-up) {
  [fe-blank] {
    &.blank-M { .h(100); }
    &.blank-L { .h(160); }
  }
}
@media (@dm-up) {
  [fe-blank] {
    &.blank-M { .h(140); }
    &.blank-L { .h(240); }
  }
}
</style>