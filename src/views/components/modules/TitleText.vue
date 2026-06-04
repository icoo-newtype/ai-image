<script setup lang="ts">

import ImageSelect from '@/views/components/form/ImageSelect.vue';
import { inject, ref, watchEffect } from 'vue';
import TextBox from '@/views/components/form/TextBox.vue';
import BasicText from '@/views/components/modules/BasicText.vue';
import { SelectItem } from '@/types/components';
import { getValue } from '@/utils/componentUtil';

const props = defineProps({
  modelValue: { type: Object, default: {} },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();

const change = () => {
  emit('update:modelValue', data.value);
};

watchEffect(() => {
  data.value = props.modelValue || {};
  emit('update:modelValue', data.value);
});
</script>

<template>
  <div title-text :class="type">
    <TextBox placeholder="타이틀을 입력해주세요." v-model="data.title" @input="change" no-resize/>
    <BasicText v-model="data.text" @input="change"/>
  </div>
</template>
