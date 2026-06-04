<script setup lang="ts">

import BRow from '@/views/components/common/BRow.vue';
import BCol from '@/views/components/common/BCol.vue';
import BInputGroup from '@/views/components/common/BInputGroup.vue';
import BInputGroupAppend from '@/views/components/common/BInputGroupAppend.vue';
import BButton from '@/views/components/common/BButton.vue';
import Modal from '@/views/components/common/Modal.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import { computed, ref, watch } from 'vue';
import oax from '@/utils/oax';
import isEmpty from 'lodash-es/isEmpty';
import VideoPlayer from '@/views/components/VideoPlayer.vue';
import CdnImg from '@/views/components/CdnImg.vue';
import BSpinner from '@/views/components/common/BSpinner.vue';
import { useToast } from '@/plugins/toast';
import { filter, some } from 'lodash-es';
import { useBoolConfirm, useConfirmCancel } from '@/hooks/useInputDialog';
import { formatBytes } from '@/utils';

const cdnHost = import.meta.env.VITE_CDN_HOST;
const props = defineProps<{
  mediaType: 'image' | 'video' | 'audio' | 'resource' | 'media' | 'any';
  filekey?: string;
  modelValue?: FileItem;
  show: boolean;
}>();
const emit = defineEmits(['update:modelValue', 'update:show']);
const success = useToast({ type: 'success' });
const warning = useToast({ type: 'warning' });
const fileState = ref(0);
const focused = ref<FileItem>();
const show = computed({
  get: () => props.show,
  set: (v: boolean) => emit('update:show', v)
});
const dragOver = ref<'file' | 'other'>();
const loading = ref(false);
const keyword = ref('');
const description = ref<string>();
const descriptionEdit = ref(false);
const title = computed(() => {
  return { 'image': '이미지', 'video': '영상', 'audio': '오디오', 'resource': '리소스', 'media': '미디어', 'any': '파일' }[props.mediaType] ?? '파일';
});
const list = ref<FileItem[]>();
const filtered = computed(() => {
  if (!keyword.value) return list.value;
  const w = keyword.value.trim().toLocaleLowerCase().normalize('NFD');
  return list.value?.filter((item: any) => {
    const fileName = item?.fileName?.trim().toLocaleLowerCase().normalize('NFD') ?? '';
    const description = item?.description?.trim().toLocaleLowerCase().normalize('NFD') ?? '';
    return fileName.includes(w) || description.includes(w);
  });
});
const map = {
  image: ['image/gif', 'image/jpeg', 'image/png', 'image/svg+xml'],
  video: ['video/mp4', 'video/webm'],
  audio: ['audio/mpeg', 'audio/ogg', 'audio/wav'],
  resource: [
    'application/pdf',
    'application/msword', // .doc
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // .docx
    'application/vnd.ms-excel', // .xls
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
    'application/vnd.ms-powerpoint', // .ppt
    'application/vnd.openxmlformats-officedocument.presentationml.presentation', // .pptx
    'application/x-hwp', // .hwp (한글)
    'application/haansofthwp', // .hwp (기타 정의)
    'application/zip',
    'text/plain'
  ],
};
const accept = computed(() => {
  if (props.mediaType === 'media' || props.mediaType === 'any') return Object.values(map).flat();
  return map[props.mediaType] || [];
});
const getMediaType = (fileType: string): 'image' | 'video' | 'audio' | 'resource' | null => {
  return (Object.entries(map).find(([, types]) => types.includes(fileType))?.[0] as 'image' | 'video' | 'audio' | 'resource') ?? null;
};

const dragEnter = (e: DragEvent) => {
  dragOver.value = some<DataTransferItem>(e.dataTransfer?.items, e => e.kind === 'file') ? 'file' : 'other';
};

const dragLeave = () => {
  dragOver.value = undefined;
};

const drop = (e: DragEvent) => {
  dragOver.value = undefined;
  if (e.dataTransfer?.items) {
    uploadFiles(filter<DataTransferItem>(e.dataTransfer.items, e => e.kind === 'file').map(e => e.getAsFile()) as File[]);
  }
};

const find = (e: Event) => {
  const el = e.target as HTMLInputElement;
  if (el.files) {
    uploadFiles([...el.files]);
    fileState.value += 1;
  }
};

const confirm = useBoolConfirm();
const confirmCancel = useConfirmCancel();

const upload = async (file: File) => {
  if (!accept.value.includes(file.type)) {
    warning(`${title} 파일만 업로드 가능합니다.`);
    return 0;
  }

  const isDuplicate = list.value?.some(item =>
      item.fileName === file.name && Number(item.capacity ?? 0) === file.size
  );
  if (isDuplicate) {
    const message = `${file.name} (${formatBytes(file.size)})<br>동일한 파일이 이미 있습니다.<br>계속 하시겠습니까?`;
    if (!await confirm(message)) return 0;
  }

  await oax.multipart(`/api/admin/media/add/${props.filekey}`, { file, mediaType: getMediaType(file.type) });
  return 1;
};

const uploadFiles = async (files: File[]) => {
  loading.value = true;
  const cnt = (await Promise.all(files.map(item => upload(item)))).reduce((a: number, v) => a + (v || 0), 0);
  if (cnt > 0) {
    success(`${cnt}개의 파일이 업로드 되었습니다.`);
    await reload();
    focused.value = list.value?.[0];
    loading.value = false;
  }
};

const reload = async () => {
  const { data } = await oax.get<FileItem[]>(`/api/admin/media/list/${props.filekey}/${props.mediaType}`);
  list.value = data;
  if (focused.value) focused.value = list.value.find(item => item.sq === focused.value?.sq);
};

const focus = async (item: FileItem) => {
  if (focused.value === item) {
    focused.value = undefined;
    description.value = '';
  } else {
    focused.value = item;
    description.value = focused.value.description ?? undefined;
  }
};

const remove = async (item: FileItem) => {
  await confirmCancel('파일 삭제', `${item.fileName} (${formatBytes(+(item.capacity ?? 0))})<br>해당 파일을 삭제하시겠습니까?`);
  if (focused.value === item) focused.value = undefined;
  await oax.post(`/api/admin/media/delete/${props.filekey}`, { sq: item.sq });
  success('삭제되었습니다');
  await reload();
};

const descriptionSave = async () => {
  if (focused.value && focused.value?.description !== description.value) {
    await oax.post(`/api/admin/media/description/${props.filekey}/${focused.value.sq}`, { description: description.value });
    success('저장되었습니다.');
    await reload();
  }
  descriptionEdit.value = false;
};

const openWindow = () => {
  if (focused.value) window.open(`${cdnHost}/${focused.value.location}`);
};

const select = () => {
  emit('update:show', false);
  emit('update:modelValue', focused.value);
};

watch(show, v => {
  if (v) {
    focused.value = props.modelValue;
    reload();
  }
});
</script>
<template>
  <Modal picker size="xl" :title="`${title} 선택`" v-model="show">
    <b-row no-gutters>
      <b-col cols="6">
        <div class="filter">
          <i class="icon-magnifier"></i>
          <TextInput placeholder="Search(filename or description)" variant="outline" v-model="keyword"/>
        </div>
        <p class="mt-4 mb-0 fs-15"><b>등록된 {{ title }}</b></p>
        <ul class="h-380 media-list p-1 mt-1">
          <li :data-sq="item.sq" v-for="item in filtered" :key="item.sq" :class="{on: focused === item}" @click="focus(item)">
            <p>{{ item.fileName }}</p>
            <p>{{ item.description }}</p>
            <i class="remove bi-x-circle-fill" @click.stop="remove(item)"></i>
          </li>
          <li class="h-100 d-flex align-items-center justify-content-center pointer-none" v-if="isEmpty(filtered)">
            {{ keyword ? 'No result' : 'No list' }}
          </li>
        </ul>
      </b-col>
      <b-col cols="6">
        <b-input-group v-if="focused">
          <TextInput placeholder="Description" variant="outline" v-model="description" :readonly="!focused"/>
          <b-input-group-append>
            <b-button variant="primary" class="square" @click="descriptionSave"><i class="bi-check-lg"></i></b-button>
          </b-input-group-append>
        </b-input-group>
        <div class="preview" @dragenter="dragEnter" @dragleave="dragLeave" @dragover.prevent @drop.prevent="drop">
          <div class="cover" v-if="loading">
            <b-spinner variant="primary"/>
          </div>
          <template v-else>
            <div class="w-100 fs-50" v-if="dragOver === 'file'"><i class="icon-plus"></i></div>
            <div class="w-100 fs-50" v-else-if="dragOver === 'other'"><i class="icon-close"></i></div>
            <template v-else-if="focused">
              <cdn-img :src="focused.location" @click="openWindow" v-if="focused.mediaType === 'image'"/>
              <VideoPlayer :src="focused.location" v-else-if="focused.mediaType === 'video'"/>
              <audio controls :src="`${cdnHost}/${focused.location}`" v-else-if="focused.mediaType === 'audio'"/>
              <p v-else>미리보기가 지원되지 않는 파일입니다</p>
            </template>
            <div class="w-100" style="color: #666" v-else>
              <img src="/admin_image/module/img.svg" alt="">
              <p class="mt-2">Drag Here!</p>
            </div>
          </template>
        </div>
        <b-input-group class="select-input mt-12">
          <label class="file-control btn">
            <i class="bi-plus-circle-fill"></i>
            <input type="file" :key="fileState" multiple @change="find" :accept="accept.join(',')">
          </label>
        </b-input-group>
        <b-button variant="primary" class="btn-save" @click="select" v-if="focused">저장</b-button>
      </b-col>
    </b-row>
  </Modal>
</template>
<style lang="less">
@import '~@/less/proj.less';
[picker] { .tl;
  .modal-dialog { .w(1020); .-box; }
  .modal-content { .rel;
    &:before { .cnt; .abs; .rt; .wh(50%, 100%); .bgc(#F3F5F7); }
    .col-6 { .rel; .pr(36); }
    .col-6 + .col-6 { .pr(0); .pl(36); }
  }

  .filter { .rel;
    i { .abs; .lt(12, 11); }
    .form-control { .pl(38); }
  }
  .media-list { .auto-y; .-a(@c-light-gray); .br(3);
    li { .rel; .-a(@c-light-gray); .br(3); .p(10, 12); .pointer;
      p:first-child { .fs(14, 1.2); }
      p + p { .mt(4); .fs(13, 1.2); .c(@c-gray); }
      .remove { .abs; .rt(12, 50%); .t-yc; .o(0); transition: opacity 0.25s; .pointer; }
      &:hover { .bgc(@c-line-bg-gray);
        .remove { .o(1); }
      }
      &.on { .-a(#20a8d8) !important; }
      & + li { .mt(4); }
    }
  }
  .input-group + .preview { .mt(12); }
  .preview { .rel; .flex-center; .h(300); .flex-row; .tc; .-a(@c-light-gray); .bgc(#fff);
    img { max-width: 100%; max-height: 100%; .mh-c; .pointer; }
    > div { pointer-events: none }
    i { color: #999; }
  }
  .file-control { .mb(0); .wf; .-a(@c-light-gray); .bgc(#fff);
    input { .abs; .l(-10000); }
  }
  .cover { background: rgba(255, 255, 255, 0.2); .abs; .lt; .f; .flex; .justify-center; .items-center; }
  .btn-save { .abs; .rb(0, 0); }
}
</style>
