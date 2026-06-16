<script setup lang="ts">

import { inject, ref, watchEffect } from 'vue';
import MediaSelect from '@/views/components/form/MediaSelect.vue';
import TextBox from '@/views/components/form/TextBox.vue';

const props = defineProps({
  modelValue: { type: Array, default: null },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();
const filekey = inject('filekey') as string;

watchEffect(() => {
  data.value = props.modelValue || [];
  emit('update:modelValue', data.value);
});
</script>

<template>
  <div media :class="type">
    <div class="list" :class="`column-${((data.length - 1) % 3) + 1}`">
      <div class="col" v-for="(item, i) in data" :key="i">
        <div class="view">
          <media-select required :filekey="filekey" v-model="data[i].media" message="권장 비율 (16:9)"/>
          <div class="title" v-if="type !== 'basic'">
            <text-box placeholder="타이틀을 입력해 주세요" :maxlength="30" v-model="data[i].title"/>
          </div>
        </div>
        <text-box placeholder="캡션을 입력해 주세요" v-model="data[i].caption" class="caption" :rows="2"/>
      </div>
    </div>
  </div>
</template>
