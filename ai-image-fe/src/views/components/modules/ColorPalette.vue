<script setup lang="ts">

import { computed, ref, watch, watchEffect } from 'vue';
import TextInput from '@/views/components/form/TextInput.vue';
import TextBox from '@/views/components/form/TextBox.vue';

const props = defineProps({
  modelValue: { type: Object, default: null },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();

const length = computed(() => Number(props.type?.match(/\d+/)?.[0] ?? 0));
const hasTitle = computed(() => props.type?.includes('title'));

const initData = () => {
  const colors = Array.from({ length: length.value }, (_, i) =>
      props.modelValue?.colors?.[i] ?? { subTitle: '', color: '#', appendix: '' }
  );
  data.value = {
    title: props.modelValue?.title ?? '',
    colors,
  };
};

// 초기 실행
initData();

// type 바뀔 때만 재초기화
watch(() => props.type, initData);

// data 변경 시 emit
watch(data, (val) => emit('update:modelValue', val), { deep: true });
</script>

<template>
  <div color-palette>
    <text-input required placeholder="Color title" v-model="data.title" class="title" v-if="hasTitle"/>
    <div class="list" :class="`column-${length}`">
      <div class="item" v-for="(col, idx) in data.colors" :key="idx">
        <div class="view" :style="{ backgroundColor: `#${col.color}` }" :class="{ outline: ['FFF', 'FFFFFF'].includes(col.color.toUpperCase()) }"></div>
        <p>
          <text-input v-model="col.color" class="color"/>
          <span>#</span>
        </p>
        <text-input required placeholder="Color subtitle" v-model="col.subTitle" class="sub-title"/>
        <text-box v-model="col.appendix" placeholder="Appendix color"/>
      </div>
    </div>
  </div>
</template>
