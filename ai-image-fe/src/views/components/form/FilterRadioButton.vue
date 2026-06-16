<script setup lang="ts">
import RadioButton from '@/views/components/form/RadioButton.vue';
import { useFilterParam } from '@/hooks/useFilterParam';
import { FilterComponentEmits, FilterComponentProps, SelectComponentProps } from '@/types/components';

const props = defineProps<Omit<SelectComponentProps, 'modelValue'> & FilterComponentProps & { prop: string }>()
const emit = defineEmits<FilterComponentEmits>()
const { model, commit } = useFilterParam(props, emit);
</script>
<template>
  <RadioButton :list="list" :obj="obj" :required="required" v-model="model" @change="commit"/>
</template>