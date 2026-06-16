<script setup lang="ts">
import { ref, watchEffect } from 'vue';
import BDropdown from '@/views/components/common/BDropdown.vue';
import BDropdownItem from '@/views/components/common/BDropdownItem.vue';

const props = defineProps({
  modelValue: { type: Object, default: null },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();
const name = ref('');
const id = ref(['blank-S', 'blank-M', 'blank-L']);
const blankList = ref(['20px', '140px', '240px']);

const change = () => {
  emit('update:modelValue', data.value);
};

function blankSelect(i: number) {
  data.value.id = id.value[i];
  data.value.index = i;
  name.value = blankList.value[i];
  emit('update:modelValue', data.value);
}

watchEffect(() => {
  data.value = props.modelValue ?? { id: 'blank-M', index: 1 };
  name.value = blankList.value[data.value.index];
  emit('update:modelValue', data.value);
});
</script>
<template>
  <div blank :class="[type, data?.id]">
    <b-dropdown id="blank-dropdown" :text="name">
      <b-dropdown-item v-for="(e,i) in blankList" :key="i" @click="blankSelect(i)" :class="data.index === i ? 'active' : null">{{ e }}
      </b-dropdown-item>
    </b-dropdown>
  </div>
</template>