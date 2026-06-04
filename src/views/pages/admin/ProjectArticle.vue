<script setup lang="ts">
import { computed, provide, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useConfirm } from '@/hooks/useInputDialog';
import { useToast } from '@/plugins/toast';
import oax from '@/utils/oax';
import { ModuleItem } from '@/types/components';
import BButton from '@/views/components/common/BButton.vue';
import { useState } from '@/store/state';
import BForm from '@/views/components/common/BForm.vue';
import { PageItem } from '@/store/page';
import ModuleGroup from '@/views/components/modules/ModuleGroup.vue';
import ImageSelect from '@/views/components/form/ImageSelect.vue';
import { map } from 'lodash-es';

const state = useState();
const router = useRouter();
const { currentRoute } = useRouter();

const loading = ref(false);
const apiData = reactive<Partial<PageItem>>({});
const article = ref<ModuleItem[]>([]);

const slug = computed(() => currentRoute.value.params.slug);
const code = computed(() => currentRoute.value.params.code as string);
provide('slug', slug.value);

const confirm = useConfirm();
const success = useToast({ type: 'success' });
const warning = useToast({ type: 'error' });

const submit = async () => {
  await oax.post(`/api/admin/code`, {
    ...apiData,
    article: btoa(encodeURIComponent(JSON.stringify(article.value))),
    edit: true,
  });
};

const validator = ref();
const validate = async () => {
  if (!validator.value?.validate()) {
    await confirm('필수 입력', '프로젝트 등록을 위해 필수 정보를 모두 입력해주세요.');
    return false;
  }
  if (!article.value.length) {
    await confirm('필수 입력', '1개 이상의 모듈을 등록해주세요.');
    return false;
  }
  return true;
};

const save = async () => {
  if (!await validate()) return;
  await submit();
  await confirm('저장되었습니다.');
  location.reload();
};

const preview = () => {
  window.open(`${state.host}/${slug.value}/${code.value.replace('-', '/')}`);
};

const getData = async () => {
  loading.value = true;
  const { data } = await oax.get<PageItem>(`/api/admin/code/${slug.value}/${code.value}`);
  Object.assign(apiData, data);
  article.value = apiData.article ? JSON.parse(decodeURIComponent(atob(apiData.article))) : [];
  loading.value = false;
};

provide('filekey', 'PROJ');
getData();

const anchors = computed(() => article.value.filter(item => item.type === 'title' || item.type === 'header'));

watch(() => article.value, () => {
}, { deep: true });

</script>

<template>
  <div project-article class="article animated fadeIn" v-if="!loading">
    <div class="util-wrap">
      <b-button size="sm" variant="outline" @click="router.push(`/admin/project/article/${apiData.projSq}`)">취소</b-button>
      <b-button size="sm" variant="outline" @click="preview">미리보기</b-button>
      <b-button size="sm" variant="primary" @click="save">저장</b-button>
    </div>
    <BForm ref="validator">
      <div class="screen-wrap">
        <div class="lnb">
          <ul>
            <li v-for="row in anchors" :class="row.type">{{ row?.value?.title }}</li>
          </ul>
        </div>
        <div class="screen">
          <div class="body">
            <ModuleGroup v-model="article"/>
          </div>
        </div>
      </div>
    </BForm>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[project-article] {
  .screen-wrap { .p(40); .flex; .rel; }
  .lnb { .p(0, 20); .-a(#E7E7E7); .mr(20); .w(300); .-box; .fs(14, 50); .medium; .sticky; .lt(0, 90); .h(100%);
    li.title { .p(0, 16); .c(#555); }
  }
  .screen { .rel; .p(40, 20, 100); .wf; .-a(#E7E7E7);
    .hero { .flex; .space-between;
      .title + div { .w(690); }
      .col-form-label { .tl; .medium; }
      .form-control { background: none; .ph(0); .medium; .c(#111); .-a(transparent);
        &:focus { .c(#666); .-a(transparent); }
      }
      .title, .sub-title { .fs(38, 1.2); }
      .sub-title { .c(#B2B2B2);
        &::placeholder { .c(#B2B2B2); }
      }
      .description { .fs(16, 1.5); }
      ul { .mv(60); .grid(2); .-t(#EAEAEA);
        li { .pv(7); .-b(#EAEAEA);
          .form-group { .m; }
          .col-form-label { .w(90); .c(#999); }
        }
        li:nth-child(7) { grid-column: span 2; }
      }
    }
    .body { .pb(100);
      .hero { .p(20, 0);
        .title { .w(620); .mh-c; .fs(48, 1); color: #1e1e1e; .bold; .p; }
      }
      .f-add { .mh(0); .tc; .mt(36);
        &:before { .cnt; .h(1); background: #111; }
        .btn {.rel; .p(5, 21); .br-b(4); .bold; z-index: 2;}
      }
      .credits { .mt(120); .tc;
        p { .fs(18, 1.6); .medium; }
        .form-control { .mt(8); .tc }
      }
    }
  }
  .info-section { .p(0, 46); .mt(50);
    h2 { .fs(24); .bold; .mb(20); }
    .category {
      .category-row { .bgc(#F7F7F7); .-a(#F7F7F7); .p(20, 20, 18);
        p { .fs(14, 1); .semi-bold; }
        [b-checkbox] { .ib; .mt(8); .mr(16); }
        & + div { .mt(12); }
      }
    }
    .input-section, .file-section {.w(50%); .ib; .vat;}
    .file-section {.pl(30);}
    [image-select] {height: auto; .min-h(300);}

    .form-group { .mb(36);
      .col-form-label { .fs(16, 1); .bold; .mb(4); }
      .count { color: #898989; .tr; .fs(12); .mt(4);
        p { .m(0);
          span { .ib; .p(0, 2); }
        }
      }
      &.related-contents {
        .related-list {.mb(4);
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
