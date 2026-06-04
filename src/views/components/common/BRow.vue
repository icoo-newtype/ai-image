<script setup lang="ts">
defineProps<{ noGutters?: boolean }>()
</script>
<template>
  <div class="row" :class="{ 'no-gutters': noGutters }"><slot /></div>
</template>