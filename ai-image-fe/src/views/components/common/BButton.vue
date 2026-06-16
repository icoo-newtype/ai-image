<script setup lang="ts">
defineProps({
  variant: String,
  size: String,
  disabled: { type: Boolean, default: false }
});
</script>
<template>
  <button type="button" :class="[`btn-${variant ?? 'default'}`, `btn-${size ?? 'md'}`]" :disabled="disabled" class="btn">
    <slot/>
  </button>
</template>