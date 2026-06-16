<script setup lang="ts">
import { computed, onMounted, PropType, reactive, ref } from 'vue';
import { useToast } from '@/plugins/toast';

const inputSq = ref(0);
const props = defineProps({
  name: { type: String, default: null },
  modelValue: { type: Object as PropType<FileInputModel>, default: null },
  multiple: { type: Boolean, default: false },
  required: { type: Boolean, default: false },
  label: { type: String, default: '파일찾기' },
  readonly: { type: Boolean, default: false },
  vertical: { type: Boolean, default: false },
  placeholder: { type: String, default: null },
  image: { type: Boolean, default: false },
  video: { type: Boolean, default: false },
  html: { type: Boolean, default: false },
});
const emit = defineEmits(['update:modelValue']);
const model = reactive<FileInputModel>({
  toString: () => 'FILE_INPUT_MODEL',
  remove: [],
  exist: [],
  add: {},
});

const fileList = ref<FileItem[]>([]);
const btnWidth = ref('0');
const labelEl = ref();
const isFill = computed(() => !props.multiple && fileList.value.length > 0);
const invalid = computed(() => props.required && fileList.value.length === 0);
const accept = computed(() => props.image ? 'image/gif,image/jpeg,image/png'
    : props.video ? 'video/mp4'
        : props.html ? 'text/html'
            : '*'
);

const warning = useToast({ type: 'warning' });

const change = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files && target.files.length) {
    const l = target.files;
    for (let i = 0; i < l.length; i += 1) {
      const sq = `${i}${Date.now()}`;
      const file = l[i];
      if (props.image && file.size > 10 * 1024 * 1024) {
        warning('10메가 이하의 파일을 선택해주세요');
        break;
      }
      if (props.video && file.size > 30 * 1024 * 1024) {
        warning('30메가 이하의 파일을 선택해주세요');
        break;
      }

      if (props.image && !['image/gif', 'image/jpeg', 'image/png'].includes(file.type)) {
        warning('이미지 파일만 가능합니다.');
        break;
      }
      if (props.video && !['video/mp4'].includes(file.type)) {
        alert('mp4영상 파일만 가능합니다.');
        break;
      }
      if (props.html && !['text/html'].includes(file.type)) {
        alert('html 파일만 가능합니다.');
        break;
      }
      fileList.value.push({ sq, name: props.name, fileName: file.name, location: '', file, added: true });
      model.add[sq] = file;
    }
  }
  inputSq.value += 1;
  emit('update:modelValue', model);
};

const remove = (fileItem: FileItem) => {
  if (fileItem.added) {
    delete model.add[fileItem.sq];
  } else {
    model.remove.push(fileItem.sq);
    model.exist = model.exist.filter(v => v.sq !== fileItem.sq);
  }
  fileList.value = fileList.value.filter(item => item.sq !== fileItem.sq);
  emit('update:modelValue', model);
};
const update = () => {
  const v = props.modelValue;
  model.add = {};
  model.remove = [];
  model.exist = [];
  if (props.modelValue?.exist?.length) {
    fileList.value = [];
    model.exist = v.exist;
    fileList.value.push(...v.exist);
  }
  //this.$parent.fileInputThumbs[this.name] = this.fileList;
};

onMounted(() => {
  update();
  if (labelEl.value) btnWidth.value = `${labelEl.value.offsetWidth + 4}px`;
});
</script>
<template>
  <div file-input :class="{invalid, vertical}" class="form-control" :style="{'padding-right': btnWidth}">
    <span v-for="fileItem in fileList" :key="fileItem.sq">
      <a v-if="!fileItem.location">{{ fileItem.fileName }}</a>
      <a v-else :href="encodeURI(`/api/download/${fileItem.filekey}/${fileItem.sq}`)">{{ fileItem.fileName }}</a>
      <a @click="remove(fileItem)" class="icon-close vam ml-1" v-if="!readonly"></a>
    </span>
    <span v-if="(!fileList || !fileList.length) && placeholder">{{ placeholder }}</span>
    <label v-if="!isFill && !readonly" ref="label"><input type="file" :name="name" @change="change" :multiple="multiple" :accept="accept" :key="accept"><span class="btn btn-light btn-sm">{{ label }}</span></label>
    <span v-if="readonly && fileList.length === 0">파일이 없습니다.</span>
  </div>
</template>
<style lang="less">
@import "~@/less/proj.less";

[file-input] { .rel; height: auto; min-height: calc(1.5em + 0.75rem + 2px);
  input { .abs; .l(-10000); }
  & > span { .mr(10); .ib; }
  .validated &.invalid { .border-danger; }
  & > label { .abs; .rt(0.16rem, 0.16rem);
    .btn { line-height: 1.4 }
  }
}
</style>
