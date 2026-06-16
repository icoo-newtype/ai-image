<script setup lang="ts">
defineProps({
  label: { type: String, required: true },
  tooltip: { type: String, default: '' },
  sortable: { type: Boolean, default: false },
  focused: { type: Boolean, default: false },
});
</script>
<template>
  <td :class="{ focused }"><slot /></td>
</template>