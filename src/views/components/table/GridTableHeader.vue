<script setup lang="ts">
import { provide } from 'vue';

provide('on-table-header', true);
</script>
<template>
  <tr>
    <slot/>
  </tr>
</template>