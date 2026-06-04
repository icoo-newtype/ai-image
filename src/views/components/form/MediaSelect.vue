<script setup lang="ts">
import CdnImg from '@/views/components/CdnImg.vue';
import MediaPicker from '@/views/components/form/MediaPicker.vue';
import { computed, ref, watch } from 'vue';
import { getMediaTypeFromPath, imageVal } from '@/utils';
import Modal from '@/views/components/common/Modal.vue';
import BInputGroup from '@/views/components/common/BInputGroup.vue';
import BButton from '@/views/components/common/BButton.vue';
import ImageSelect from '@/views/components/form/ImageSelect.vue';
import VideoPlayer from '@/views/components/VideoPlayer.vue';

const props = defineProps<{
  filekey: string,
  modelValue?: string,
  required?: boolean,
  message?: string,
  mediaType?: 'image' | 'video' | 'audio' | 'resource' | 'media' | 'any' | undefined | null,
}>();

const emit = defineEmits(['update:modelValue']);
const showPicker = ref(false);
const info = computed<FileItem | undefined>(() => imageVal(props.modelValue));
const invalid = computed(() => props.required && !info.value);
const model = computed({
  get: () => info.value,
  set: (value: FileItem) => {
    emit('update:modelValue', `${value.location}#${value.sq}`);
  }
});
</script>
<template>
  <div image-select :class="{ invalid }">
    <div class="holder rounded d-flex align-items-center justify-content-center" @click="showPicker = true">
      <cdn-img :src="info.location" v-if="info && getMediaTypeFromPath(info.location) === 'image'"/>
      <VideoPlayer controls fit="cover" :src="info.location" v-else-if="info && getMediaTypeFromPath(info.location) === 'video'"/>
      <div v-else class="plus">
        <i class="plus-icon"></i>
        <p>Add an File</p>
        <p class="message">{{ message }}</p>
      </div>
      <div v-if="info" class="cov d-flex align-items-center justify-content-center">Change</div>
    </div>
    <MediaPicker :media-type="mediaType || 'media'" :filekey="filekey" :value="info" v-model="model" v-model:show="showPicker"/>
  </div>
</template>
<style lang="less">
@import '~@/less/proj.less';
[image-select] {
  .holder { .f; .min-w(100); .min-h(300); .rel; overflow: hidden; background-color: #fff; cursor: pointer; .-a(#cfcfcf); border-style: dashed;
    img { .block; max-width: 100%; }
    .cov { .o(0); transition: opacity 0.3s; background: rgba(0, 0, 0, 0.5); .abs; .lt; .f; color: #fff; .pointer; }
    &:hover .cov { opacity: 1; }
    .plus {.tc; .mt(30);
      p {.fs(14);
        span {.fs(12); color: #898989;}
        &.message {.fs(22); color: #cfcfcf;}
      }
      i {.wh(40, 40); .bg('/admin_image/common/plus-circle.png'); .contain; .block; .m(0, auto, 12); transition: all .3s ease;}
    }
    &:hover {
      .plus {
        i {.bg('/admin_image/common/plus-circle-hover.png');}
      }
    }
  }
  .validated &.invalid .holder { .border-danger; }
}

</style>
