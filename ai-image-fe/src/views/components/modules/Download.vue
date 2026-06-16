<script setup lang="ts">
import { inject, ref, watchEffect } from 'vue';
import BDropdown from '@/views/components/common/BDropdown.vue';
import BDropdownItem from '@/views/components/common/BDropdownItem.vue';
import BButton from '@/views/components/common/BButton.vue';
import MediaPicker from '@/views/components/form/MediaPicker.vue';

const props = defineProps({
  modelValue: { type: Object, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();
const showMediaPicker = ref(false);
const filekey = inject('filekey') as string;

const addResource = (file: FileItem) => {
  emit('update:modelValue', file);
};

watchEffect(() => {
  data.value = props.modelValue || {};
});
</script>
<template>
  <div download>
    <b-button variant="primary" @click="showMediaPicker = true">Download</b-button>
    <MediaPicker media-type="resource" :filekey="filekey" v-model:show="showMediaPicker" @update:model-value="addResource"/>
  </div>
</template>