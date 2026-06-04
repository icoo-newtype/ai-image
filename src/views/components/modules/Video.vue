<script setup lang="ts">

import { inject, ref, watch, watchEffect } from 'vue';
import MediaSelect from '@/views/components/form/MediaSelect.vue';
import TextBox from '@/views/components/form/TextBox.vue';

const props = defineProps({
  modelValue: { type: Object, default: null },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();
const filekey = inject('filekey') as string;

watch(() => props.modelValue, (v) => {
  data.value = v || { media: '', caption: '' };
}, { immediate: true });

watch(data, (v) => {
  emit('update:modelValue', v);
}, { deep: true });
</script>

<template>
  <div video :class="type">
    <div class="view">
      <media-select required media-type="video" :filekey="filekey" v-model="data.media" message="권장 비율 (16:9)"/>
      <div class="title" v-if="type !== 'basic'">
        <text-box placeholder="타이틀을 입력해 주세요" :maxlength="30" v-model="data.title"/>
      </div>
    </div>
    <text-box placeholder="캡션을 입력해 주세요" v-model="data.caption" class="caption" :rows="2"/>
  </div>
</template>
