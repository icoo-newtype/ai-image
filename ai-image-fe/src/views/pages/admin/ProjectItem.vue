<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useConfirm } from '@/hooks/useInputDialog';
import { useToast } from '@/plugins/toast';
import oax from '@/utils/oax';
import BButton from '@/views/components/common/BButton.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import BInputGroup from '@/views/components/common/BInputGroup.vue';
import { useState } from '@/store/state';
import TextBox from '@/views/components/form/TextBox.vue';
import BForm from '@/views/components/common/BForm.vue';
import BInputGroupPrepend from '@/views/components/common/BInputGroupPrepend.vue';
import { reloadPage } from '@/utils/routeUtil';
import CodeManager from '@/views/components/CodeManager.vue';
import BFormGroup from '@/views/components/common/BFormGroup.vue';
import DropSelect from '@/views/components/form/DropSelect.vue';
import ImageSelect from '@/views/components/form/ImageSelect.vue';
import RadioButton from '@/views/components/form/RadioButton.vue';
import { useModal } from '@/plugins/modal';
import PasswordModal from '@/views/components/modal/PasswordModal.vue';
import Drag from '@/views/components/article/Drag.vue';
import MediaPicker from '@/views/components/form/MediaPicker.vue';

const state = useState();
const router = useRouter();
const { currentRoute } = useRouter();

const loading = ref(false);
const apiData = reactive<Partial<ProjectItem>>({ access: 'Y' });
const resourceList = ref<FileItem[]>();
const showMediaPicker = ref(false);

const sq = computed(() => currentRoute.value.params.sq);

const confirm = useConfirm();
const success = useToast({ type: 'success' });
const warning = useToast({ type: 'error' });

const copySlug = async () => {
  await navigator.clipboard.writeText(`${state.host}/${apiData.slug}`);
  success('URL copied to clipboard.');
};

const addResource = (file: FileItem) => {
  resourceList.value = [...resourceList.value ?? [], file];
};

const validator = ref();
const validate = async () => {
  if (!validator.value?.validate()) {
    await confirm('필수 입력', '프로젝트 등록을 위해 필수 정보를 모두 입력해주세요.');
    return false;
  }
  if (apiData.slug && /[`~!@#$%^&*()+[\]{}\\|=,./<>?;':"\s]+/.test(apiData.slug)) {
    await confirm('URL Slug', 'URL Slug에는 공백 및 특수문자를 포함할 수 없습니다.(-,_ 제외)');
    return false;
  }
  return true;
};

const save = async () => {
  if (!await validate()) return;
  const { data } = await oax.post<ProjectItem>(`/api/admin/project`, {
    ...apiData,
    resource: btoa(encodeURIComponent(JSON.stringify(resourceList.value ?? []))),
  });
  await confirm('저장되었습니다.');
  if (!sq.value) await router.push(`/admin/project/article/${data.sq}`);
  else location.reload();
};

const getData = async () => {
  loading.value = true;
  const { data } = await oax.get<ProjectItem>(`/api/admin/project/${sq.value}`);

  Object.assign(apiData, data);
  resourceList.value = JSON.parse(decodeURIComponent(atob(data.resource as string)));
  loading.value = false;

};

watch(() => apiData.title, (val) => {
  if (apiData.sq) return;
  apiData.slug = val?.replace(/([A-Z])/g, (c: string) => c.toLowerCase()).replace(/\s+/g, '-').replace(/[^ㄱ-힣\w-]/g, '');
});

const passwordModal = useModal<string, {}>(PasswordModal);
const openPasswordModal = async () => {
  apiData.password = await passwordModal();
};

reloadPage();
if (sq.value) getData();
</script>

<template>
  <div project-item class="article animated fadeIn" v-if="!loading">
    <BForm ref="validator">
      <div class="init" v-if="!apiData.sq">
        <div class="center-warp">
          <TextBox required class="title" placeholder="프로젝트명을 입력해주세요" v-model="apiData.title" no-resize/>
          <b-input-group class="mt-20 w-450">
            <b-input-group-prepend class="pr-2">
              {{ `${state.host}/` }}
            </b-input-group-prepend>
            <TextInput required v-model="apiData.slug" placeholder="URL Slug를 입력해 주세요"/>
          </b-input-group>
          <b-button variant="primary" class="write-btn mt-50" @click="save">새 프로젝트 생성</b-button>
        </div>
      </div>
      <div class="setup" v-else>
        <TextBox required class="title" placeholder="프로젝트명을 입력해주세요" v-model="apiData.title" no-resize/>
        <a :href="`${state.host}/${apiData.slug}`" target="_blank">{{ `${state.host}/${apiData.slug}` }}</a>
        <code-manager :sq="apiData.sq" :slug="apiData.slug"/>
        <h2 class="mt-100">기본 정보</h2>
        <div class="grid">
          <BFormGroup horizontal label="타입 선택" class="required">
            <DropSelect required class="w-200" v-model="apiData.type" :list="['Essential','Professional','Enterprise']"/>
          </BFormGroup>
          <BFormGroup horizontal label="고객사" class="required">
            <TextInput required v-model="apiData.client" placeholder="고객사를 입력해 주세요"/>
          </BFormGroup>
          <BFormGroup horizontal label="공개 설정" class="required">
            <RadioButton required v-model="apiData.access" :obj="{ 'S': '외부 공유', 'Y': '공개', 'N': '비공개' }"/>
          </BFormGroup>
          <BFormGroup horizontal label="비밀번호" class="required">
            <!--            <b-button variant="outline" @click="openPasswordModal">비밀번호 {{ apiData.hasPassword ? '변경' : '등록' }}</b-button>-->
            <TextInput v-model="apiData.password" placeholder="비밀번호를 입력해 주세요"/>
          </BFormGroup>
          <BFormGroup horizontal label="로고 이미지 <br>(GNB용)">
            <ImageSelect :filekey="apiData.slug as string" v-model="apiData.logoImage" class="logo"/>
            <p class="description">권장 이미지 크기 480x90 | 용량 1MB</p>
          </BFormGroup>
          <BFormGroup horizontal label="로고 이미지 <br>(Footer용)">
            <ImageSelect :filekey="apiData.slug as string" v-model="apiData.footerLogoImage" class="logo black"/>
            <p class="description">권장 이미지 크기 480x90 | 용량 1MB</p>
          </BFormGroup>
          <BFormGroup horizontal label="OG 이미지" class="combine">
            <ImageSelect :filekey="apiData.slug as string" v-model="apiData.ogImage"/>
            <p class="description">권장 이미지 크기 800x420 | 용량 2MB</p>
          </BFormGroup>
          <BFormGroup horizontal label="리소스" class="combine">
            <div class="resource-list">
              <Drag v-model="resourceList" handle=".handle">
                <div class="list-group-item" v-for="(element,i) in resourceList" :key="i">
                  <i class="bi-list handle mr-1"></i>
                  {{ element.fileName }}
                  <i class="bi-x-circle-fill x-btn text-black-50" @click="resourceList?.splice(i,1)"></i>
                </div>
              </Drag>
              <b-button variant="outline" class="block wf mt-1" @click="showMediaPicker = true">
                <i class="bi-plus-circle-fill text-black-50"></i>
              </b-button>
            </div>
            <MediaPicker media-type="resource" :filekey="apiData.slug" v-model:show="showMediaPicker" @update:model-value="addResource"/>
          </BFormGroup>
        </div>
        <div class="d-flex justify-content-center mt-100">
          <b-button variant="outline" @click="router.push(`/admin/project/list`)">{{ sq ? '목록' : '취소' }}</b-button>
          <b-button variant="primary" @click="save" class="ml-10">저장</b-button>
        </div>
      </div>
    </BForm>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[project-item] {
  .init { .flex-center; .p(0, 40); .max-w(1200); .mh-c; .min-h(calc(100vh - 200px));
    .center-warp { .wf; .tc; }
    .title { .fs(48, 1.5); .tc; .bold; }
    .input-group { .mh-c; }
  }
  .setup { .p(0, 40); .mt(50);
    .title { .fs(36, 1.5); .bold; .w(600); .bgc(#fff); .-a; .p; }
    .title + a:hover { .underline; }
    h2 { .fs(24); .bold; .mb(20); }
    .category {
      .category-row { .bgc(#F7F7F7); .-a(#F7F7F7); .p(20, 20, 18);
        p { .fs(14, 1); .semi-bold; }
        [b-checkbox] { .ib; .mt(8); .mr(16); }
        & + div { .mt(12); }
      }
    }

    .grid { .grid(2); .-t(#e7e7e7);
      .combine { grid-column: span 2; }
      .form-group { .-b(#e7e7e7); .m;
        &.required .col-form-label:after { content: '*'; .ib; .ml(5); .c(#F86C6C); }
        .col-form-label { .fs(13, 1); .min-h(50); .flex; .items-center; .-box; .w(120); .ph(12); .bgc(#F9FAFB); .c(#898989); font-weight: normal;}
        .col-form-label + div { .p(8, 12); }
        [image-select] { .w(600); .min-h(300);
          &.logo { .w(200); .min-h(auto);
            .holder { .min-h(120); }
            &.black .holder { .bgc(#111); }
          }
        }
        .description { .mt(5); .fs(12, 1.4); .medium; .c(#898989); }

        .count { color: #898989; .tr; .fs(12); .mt(4);
          p { .m(0);
            span { .ib; .p(0, 2); }
          }
        }
        .resource-list {.mb(4); .w(50%);
          .list-group-item {.p(10, 15); .rel;
            .handle {.mr(5);}
            .x-btn {.abs; .rt(10, 12); cursor: pointer;}
          }
        }
      }
    }
  }
  .validated .category-row.invalid { .border-danger; }
}

</style>
