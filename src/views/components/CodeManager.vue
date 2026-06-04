<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import oax from '@/utils/oax';
import { PageItem, PageResponse, usePage } from '@/store/page';
import { flatten } from 'lodash-es';
import { useToast } from '@/plugins/toast';
import { useConfirm } from '@/hooks/useInputDialog';
import BButton from '@/views/components/common/BButton.vue';
import BRow from '@/views/components/common/BRow.vue';
import BCol from '@/views/components/common/BCol.vue';
import BCard from '@/views/components/common/BCard.vue';
import isEmpty from 'lodash-es/isEmpty';
import Drag from '@/views/components/article/Drag.vue';
import Modal from '@/views/components/common/Modal.vue';
import BFormGroup from '@/views/components/common/BFormGroup.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import RadioButton from '@/views/components/form/RadioButton.vue';
import BForm from '@/views/components/common/BForm.vue';
import BInputGroupPrepend from '@/views/components/common/BInputGroupPrepend.vue';
import BInputGroup from '@/views/components/common/BInputGroup.vue';
import { useState } from '@/store/state';
import { useRouter } from 'vue-router';

const router = useRouter();
const props = defineProps<{
  sq: number;
  slug?: string;
}>();

const state = useState();
const editDepth = ref(0);
const editOpen = ref(false);
const detailInfo = ref<Partial<PageItem>>({ projSq: props.sq });
const depths = ref<string[]>(['ROOT']);
const depthLabels = ref<string[]>(['GNB 메뉴']);
const depthsData = reactive<PageItem[][]>([]);
const reorder = ref(false);
let orgData: PageItem[][];

const confirm = useConfirm();
const success = useToast({ type: 'success' });
const warning = useToast({ type: 'error' });

const fullRoutePath = computed(() => {
  const { host } = state;
  const { parentCode, edit, code } = detailInfo.value;

  let url = `${host}/${props.slug}`;

  if (parentCode && parentCode !== 'ROOT') {
    url += `/${codeToPath(parentCode as string)}`;
  }

  if (edit && code) {
    const lastCodePart = code.split('-').at(-1);
    url += `/${lastCodePart}`;
  } else {
    url += '';
  }

  return url;
});

const codeToPath = (str: string) => {
  return str.replace(/-/g, '/');
};

const startReorder = () => {
  orgData = depthsData.concat();
  reorder.value = true;
};

const saveReorder = () => {
  oax.postJson(`/api/admin/code/order/${props.sq}`,
      flatten(depthsData.map(col => col.map((row, idx) => ({ odr: idx + 1, code: row.code, projSq: row.projSq })))),
  ).then(() => {
    reorder.value = false;
    success('저장되었습니다');
  });
};

const cancelReorder = () => {
  Object.assign(depthsData, orgData);
  reorder.value = false;
};

const updateList = async (depth: number) => {
  depths.value = depths.value.slice(0, depth + 1);
  depthsData.length = depth;
  const { data } = await oax.get<PageResponse[]>(`/api/admin/code/list/${props.sq}/${depths.value[depth]}`);
  depthsData.push(data.map(row => ({ ...row, route: row.code.split('-').at(-1), data: JSON.parse(row.data) } as PageItem)));
};

const expand = (item: PageItem, depth: number) => {
  if (reorder.value) return;
  if (depth > 0) return; // 2Depth 까지만 열림
  depths.value[depth + 1] = item.code;
  depthLabels.value[depth + 1] = item.label;
  updateList(depth + 1);
};

const editItem = (item: PageItem, depth: number) => {
  if (reorder.value) return;
  detailInfo.value = { ...item, edit: true };
  oax.fillFile(detailInfo.value);
  editDepth.value = depth;
  editOpen.value = true;
};

const register = (depth: number) => {
  detailInfo.value = {
    edit: false,
    parentCode: depths.value[depth],
    label: '',
    data: {},
    access: 'Y',
  };
  editDepth.value = depth;
  editOpen.value = true;
};

const closeDetail = () => {
  editOpen.value = false;
};
const deleteItem = async () => {
  await confirm('정말 삭제하시겠습니까?');
  await oax.delete('/api/admin/code', detailInfo.value);
  success('삭제 되었습니다.');
  closeDetail();
  await updateList(editDepth.value);
};

const validator = ref();
const save = async () => {
  if (!detailInfo.value) return;
  if (!validator.value?.validate()) {
    warning('필수 항목을 입력해주세요');
    return;
  }
  let code = detailInfo.value.code;
  if (!detailInfo.value.edit) {
    if (code && /[`~!@#$%^&*()+_ [\]{}\\|=,./<>?;':"\s-]+/.test(code)) {
      await confirm('경로 설정', '경로에는 공백 및 특수문자를 포함할 수 없습니다.');
      return false;
    }
    code = detailInfo.value.parentCode === 'ROOT' ? `${code}` : `${detailInfo.value.parentCode}-${code}`;
  }
  await oax.post('/api/admin/code', { ...detailInfo.value, code, projSq: props.sq, data: JSON.stringify(detailInfo.value.data) });
  success('저장되었습니다');
  closeDetail();
  await updateList(editDepth.value);
};

const shrink = (child: string, parent: string) => {
  return child.replace(`${parent}-`, '');
};

updateList(0);
</script>
<template>
  <div code>
    <div class="d-flex mt-5">
      <template v-if="reorder">
        <b-button variant="danger" @click="cancelReorder">취소</b-button>
        <b-button variant="primary" class="ml-1" @click="saveReorder">순서변경 저장</b-button>
      </template>
      <b-button variant="default" @click="startReorder" v-else>순서변경</b-button>
    </div>
    <b-row class="mt-2">
      <b-col cols="12" v-for="(code, depth) in depths" :key="code">
        <b-card :header="depthLabels[depth]">
          <div class="list-group" v-if="depthsData[depth]">
            <Drag v-model:list="depthsData[depth]" handle=".handle">
              <template v-for="item in depthsData[depth]" :key="item.code">
                <div class="list-group-item p-3 d-flex justify-content-between align-items-center" :class="{on: item.code === depths[depth + 1], pointer: depth < 3, dim: item.access !== 'Y'}" @click.stop="router.push(`/admin/project/${props.slug}/${item.code}`)">
                  <span>
                    <i class="bi-list handle mr-1" v-if="reorder"></i>
                    {{ item.label }}
                    <template v-if="false">[{{ shrink(item.code, code) }}]</template>
                  </span>
                  <span>
                    <b-button variant="ghost" class="p-0 mr-3" @click.stop="router.push(`/admin/project/${props.slug}/${item.code}`)">
                      <i class="bi-pencil-square text-black-50"></i>
                    </b-button>
                    <b-button variant="ghost" class="p-0" @click.stop="editItem(item, depth)">
                      <i class="bi-gear-fill text-black-50"></i>
                    </b-button>
                  </span>
                  <i class="icon-arrow-right expand" v-if="item.code === depths[depth + 1]"></i>
                </div>
              </template>
            </Drag>
            <div class="list-group-item tc p-3" v-if="!depthsData[depth]"></div>
            <div class="tc list-group-item" v-else-if="isEmpty(depthsData[depth])">
              등록된 항목이 없습니다
            </div>
          </div>
          <!--            <b-button variant="default" class="mt-10 w-100" @click="register(depth)" v-if="!reorder">항목추가</b-button>-->
          <b-button class="mt-10 block wf" variant="outline" @click="register(depth)" v-if="!reorder">
            <i class="bi-plus-circle-fill"></i>
          </b-button>
        </b-card>
      </b-col>
    </b-row>
  </div>

  <Modal v-model="editOpen" :title="detailInfo?.edit ? '메뉴 수정' : '메뉴 등록'">
    <BForm class="register-modal" v-if="detailInfo" ref="validator">
      <b-form-group label="URL" :label-cols="3" horizontal>
        <b-input-group-prepend class="pr-2 pt-1">
          {{ fullRoutePath }}
        </b-input-group-prepend>
        <TextInput required v-model="detailInfo.code" class="mt-2" v-if="!detailInfo.edit"/>
      </b-form-group>
      <b-form-group label="메뉴명" :label-cols="3" horizontal>
        <TextInput required type="text" v-model="detailInfo.label"/>
      </b-form-group>
      <b-form-group label="공개 설정" :label-cols="3" horizontal>
        <RadioButton v-model="detailInfo.access" :obj="{ 'Y':'공개', 'N':'비공개' }"/>
      </b-form-group>
      <div class="mt-40 text-right">
        <b-button class="w-90 ml-1" @click="deleteItem" variant="danger" v-if="detailInfo.edit">삭제</b-button>
        <b-button class="w-90 ml-1" @click="save" variant="primary">저장</b-button>
      </div>
    </BForm>
  </Modal>
</template>
<style lang="less">
@import '~@/less/proj.less';

[code] {
  .icon-arrow-right.expand { .abs; .r(-30); }
  .card { border-color: @c-border;
    .card-header { border-color: @c-border; .medium; }
    .card-body { .p(8); }
  }
  .list-group {
    .list-group-item { transition: background-color .2s ease;
      &:hover { .bgc(#f6f6f6); }
      &.on { .bgc(#e3f0f7); }
      &.dim { color: #666; }

      .btn { .fs(16); }
    }
  }
  .pointer { .pointer; }
}
</style>