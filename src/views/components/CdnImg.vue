<script setup lang="ts">
const cdnHost = import.meta.env.VITE_CDN_HOST;
defineProps<{
  src: string;
  alt?: string;
}>()
</script>
<template>
  <img cdn-img :src="`${cdnHost}/${src}`" :alt="alt">
</template>
<style lang="less">
@import "~@/less/proj.less";

[cdn-img] img { .block; max-width: 100%; }
</style>