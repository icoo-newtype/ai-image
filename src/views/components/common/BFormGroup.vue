<script setup lang="ts">
defineProps<{ horizontal?: boolean, labelFor?: string, label?: string, labelCols?: number, description?: string | null }>();
</script>
<template>
  <div class="form-group" :class="{ 'form-row': horizontal }">
    <label v-if="label" :for="labelFor" class="col-form-label" :class="`col-${labelCols}`" v-html="label"></label>
    <div :class="{ col: horizontal }">
      <slot/>
      <small v-if="description" tabindex="-1" class="form-text text-muted">{{ description }}</small>
    </div>
  </div>
</template>