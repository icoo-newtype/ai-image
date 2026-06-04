<script setup lang="ts">
import BButtonGroup from '@/views/components/common/BButtonGroup.vue';
import { map } from 'lodash-es';
import BButton from '@/views/components/common/BButton.vue';
import { computed } from 'vue';
import { SelectComponentEmits, SelectComponentProps, SelectItem } from '@/types/components';
import { getLabel, getValue } from '@/utils/componentUtil';

const props = defineProps<SelectComponentProps>();
const emit = defineEmits<SelectComponentEmits>();
const generated = computed(() => {
  if (props.obj) return map(props.obj, (label, value) => ({ value, label }));
  return props.list;
});

const change = (item: SelectItem) => {
  const v = getValue(item);
  emit('update:modelValue', v);
  emit('change', v);
};

</script>
<template>
  <b-button-group :class="{ error: required && !modelValue }" radio-button>
    <b-button v-for="item in generated" :key="getValue(item) ?? undefined" :variant="modelValue === getValue(item) ? 'primary' : 'default'" @click.prevent.stop="change(item)">
      {{ getLabel(item) }}
    </b-button>
  </b-button-group>
</template>

<style lang="less">
@import '~@/less/proj.less';

[radio-button] {
  .btn { .w(auto); }
}
.required [radio-button].error .btn {
  .-a(@c-red) !important;
}
</style>
