<script setup lang="ts">
defineProps<{ to?: string, click?: (e: MouseEvent) => void }>()
</script>
<template>
  <li><a :href="to" class="dropdown-item" target="_self" @click="click"><slot /></a></li>
</template>