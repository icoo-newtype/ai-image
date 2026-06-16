<script setup lang="ts">
import { useState } from '@/store/state';

const state = useState();
</script>
<template>
  <div id="app" :class="state.lang">
    <RouterView />
  </div>
</template>
<style lang="less">
@import "@/less/common.less";

</style>