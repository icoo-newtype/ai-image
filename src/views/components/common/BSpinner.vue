<script setup lang="ts">
defineProps<{ small?: boolean; variant: Variant }>()
</script>
<template>
  <span class="spinner-border" :class="[{ 'spinner-border-sm': small }, `text-${variant}`]"></span>
</template>