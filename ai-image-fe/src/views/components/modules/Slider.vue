<script setup lang="ts">

import { inject, ref, watchEffect } from 'vue';
import BButton from '@/views/components/common/BButton.vue';
import MediaSelect from '@/views/components/form/MediaSelect.vue';

const props = defineProps({
  modelValue: { type: Array, default: [] },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();
const slug = inject('slug') as string;

const add = () => {
  data.value.push('');
};

const remove = (n: number) => {
  data.value.splice(n, 1);
};

watchEffect(() => {
  data.value = props.modelValue || [];
  emit('update:modelValue', data.value);
});
</script>

<template>
  <div slider :class="type">
    <div class="grid-wrap">
      <div v-for="(row, i) in data" :key="i">
        <MediaSelect :filekey="slug" v-model="data[i]"/>
        <b-button variant="ghost" @click="remove(i)"><i class="bi-x-circle-fill"></i></b-button>
      </div>
      <b-button variant="outline" @click="add">
        <i class="bi-plus-circle-fill"></i>
      </b-button>
    </div>
  </div>
</template>
