<script setup lang="ts">
import { useFilterParam } from '@/hooks/useFilterParam';
import { FilterComponentEmits, FilterComponentProps, SelectComponentProps } from '@/types/components';
import DropSelect from '@/views/components/form/DropSelect.vue';

const props = defineProps<Omit<SelectComponentProps, 'modelValue'> & FilterComponentProps & { prop: string }>()
const emit = defineEmits<FilterComponentEmits>()
const { model, commit } = useFilterParam(props, emit);
</script>
<template>
  <DropSelect v-if="list?.length" :placeholder="placeholder" :list="list" :required="required" v-model="model" @change="commit"/>
</template>