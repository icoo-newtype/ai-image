<script setup lang="ts">

import ImageSelect from '@/views/components/form/ImageSelect.vue';
import { inject, ref, watchEffect } from "vue";
import BRow from "@/views/components/common/BRow.vue";
import BCol from "@/views/components/common/BCol.vue";

const props = defineProps({
  modelValue: { type: Object, default: {} },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();
const type = inject('type');

watchEffect(() => {
  data.value = props.modelValue || {};
  emit('update:modelValue', data.value);
});
</script>

<template>
  <div double-image :class="type">
    <b-row>
      <b-col cols="6"><ImageSelect required :class="data.left ? 'filled' : ''" :filekey="type" v-model="data.left" /></b-col>
      <b-col cols="6"><ImageSelect required :class="data.right ? 'filled' : ''" :filekey="type" v-model="data.right" /></b-col>
    </b-row>
  </div>
</template>
