<script setup lang="ts">

import { computed, inject, ref, watchEffect } from 'vue';
import MediaSelect from '@/views/components/form/MediaSelect.vue';
import TextBox from '@/views/components/form/TextBox.vue';
import ImageSelect from '@/views/components/form/ImageSelect.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import DropSelect from '@/views/components/form/DropSelect.vue';
import MediaPicker from '@/views/components/form/MediaPicker.vue';
import BButton from '@/views/components/common/BButton.vue';

const props = defineProps({
  modelValue: { type: Array, default: null },
  type: { type: String, default: null },
});
const emit = defineEmits(['update:modelValue']);

const data = ref();
const filekey = inject('filekey') as string;
const pickerIndex = ref(0);
const showMediaPicker = ref(false);


const addResource = (file: FileItem) => {
  // emit('update:modelValue', file);
  data.value[pickerIndex.value].capacity = file.capacity;
  data.value[pickerIndex.value].location = file.location;
  showMediaPicker.value = false;
};

watchEffect(() => {
  data.value = props.modelValue || [{ thumb: '', filename: '', size: '', type: '', capacity: '', location: '' }, {
    thumb: '',
    filename: '',
    size: '',
    type: '',
    capacity: '',
    location: ''
  }];
  emit('update:modelValue', data.value);
});
</script>

<template>
  <div resources :class="type">
    <div class="list">
      <div class="col" v-for="(item, i) in data" :key="i" :class="{ invalid: data[i].type && !data[i].location }">
        <image-select :filekey="filekey" v-model="data[i].thumb"/>
        <div>
          <div class="d-flex">
            <DropSelect class="w-120 mr-2" v-model="data[i].type" placeholder="파일 구분" :list="['Image','Video','PDF','etc']"/>
            <b-button class="btn-upload" variant="default" @click="pickerIndex = i; showMediaPicker = true">{{ data[i].location ? 'Change' : 'Upload'
              }} File
            </b-button>
          </div>
          <text-input placeholder="파일명을 입력해 주세요" v-model="data[i].filename"/>
          <text-input placeholder="파일 사이즈를 단위와 함께 입력해 주세요." v-model="data[i].size"/>
        </div>
      </div>
    </div>
    <MediaPicker media-type="any" :filekey="filekey" v-model:show="showMediaPicker" @update:model-value="addResource"/>
  </div>
</template>
