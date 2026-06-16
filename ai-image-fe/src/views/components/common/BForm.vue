<script setup lang="ts">
import { ref } from 'vue';

const el = ref<HTMLElement>();
const wasValidated = ref(false);
defineExpose({
  validate: (silent?: boolean) => {
    if (!el.value) return false;
    if (!silent) wasValidated.value = true;
    const invalid = el.value.querySelector('input:invalid, textarea:invalid, .invalid') as HTMLElement;
    if (invalid) invalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
    return !invalid;
  },
  reset: () => {
    wasValidated.value = false;
  }
});
</script>
<template>
  <div ref="el" :class="{ 'validated': wasValidated }"><slot /></div>
</template>