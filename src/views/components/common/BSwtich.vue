<template>
  <label class="app-switch" :class="[variant, size, { disabled }]">
    <input type="checkbox" :checked="modelValue" :disabled="disabled" @change="$emit('update:modelValue', ($event.target as HTMLInputElement).checked)"/>
    <span class="track">
      <span class="thumb"/>
    </span>
    <span v-if="label" class="label-text">{{ label }}</span>
  </label>
</template>

<script setup lang="ts">
defineProps<{
  modelValue?: boolean
  label?: string
  variant?: 'success' | 'danger' | 'warning' | ''
  size?: 'sm' | 'lg' | ''
  disabled?: boolean
}>();

defineEmits<{
  'update:modelValue': [value: boolean]
}>();
</script>

<style scoped>
.app-switch {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.app-switch input { display: none; }

.track {
  position: relative;
  width: 40px;
  height: 22px;
  border-radius: 11px;
  background: #b1b7c1;
  transition: background 0.2s;
  flex-shrink: 0;
}

.thumb {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fff;
  transition: transform 0.2s;
}

input:checked ~ .track { background: #321fdb; }

input:checked ~ .track .thumb { transform: translateX(18px); }

.success input:checked ~ .track { background: #2eb85c; }

.danger input:checked ~ .track { background: #e55353; }

.warning input:checked ~ .track { background: #f9b115; }

.disabled { opacity: 0.45; cursor: not-allowed; pointer-events: none; }

.sm .track { width: 30px; height: 17px; border-radius: 9px; }

.sm .thumb { width: 11px; height: 11px; }

.sm input:checked ~ .track .thumb { transform: translateX(13px); }

.lg .track { width: 52px; height: 28px; border-radius: 14px; }

.lg .thumb { width: 20px; height: 20px; top: 4px; left: 4px; }

.lg input:checked ~ .track .thumb { transform: translateX(24px); }

.label-text { font-size: 16px; }
</style>