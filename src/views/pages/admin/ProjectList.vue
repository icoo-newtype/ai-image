<script setup lang="ts">
import BRow from '@/views/components/common/BRow.vue';
import BCol from '@/views/components/common/BCol.vue';
import { ref } from 'vue';
import SearchBlock from '@/views/components/form/SearchBlock.vue';
import GridTable from '@/views/components/table/GridTable.vue';
import GridTableColumn from '@/views/components/table/GridTableColumn.vue';
import BButton from '@/views/components/common/BButton.vue';
import { useStoredParam } from '@/hooks/useStoredParam';
import oax from '@/utils/oax';
import { formDateTime } from '@/utils/timeUtil';
import { useConfirmCancel } from '@/hooks/useInputDialog';
import { useToast } from '@/plugins/toast';
import FilterDropSelect from '@/views/components/form/FilterDropSelect.vue';
import { useRouter } from 'vue-router';

const confirm = useConfirmCancel();
const warning = useToast({ type: 'error' });
const success = useToast({ type: 'success' });
const router = useRouter();

const listParam = useStoredParam({ perPage: 10 });
const list = ref<ProjectItem[]>([]);
const selectedIds = ref<string[]>([]);
const paging = ref();
const loading = ref(false);
const getList = async () => {
  loading.value = true;
  const { data } = await oax.get<PagingList<ProjectItem>>('/api/admin/project', listParam.value);
  list.value = data.list;
  paging.value = data.paging;
  loading.value = false;
};

const remove = async () => {
  if (!selectedIds.value.length) return warning('선택된 항목이 없습니다');
  await confirm('게시물 삭제', `선택한 ${selectedIds.value.length}개의 항목을 삭제하시겠습니까?`);
  await oax.post(`/api/admin/project/delete`, { sq: selectedIds.value });
  success('삭제되었습니다');
  await getList();
};

const changePage = (v: number) => {
  listParam.value.pageNo = v;
  getList();
};

getList();
</script>

<template>
  <div project-list class="wrapper">
    <div class="animated fadeIn">
      <div class="inner">
        <h1>Project list</h1>
        <b-row class="justify-content-between mb-50">
          <b-col cols="8" class="d-flex">
            <FilterDropSelect v-model="listParam" :list="['Essential','Professional','Enterprise']" class="w-150 mr-2" placeholder="모든 타입" prop="type" @change="changePage(1)"/>
            <FilterDropSelect v-model="listParam" :list="[{label: '외부 공유', value: 'S'},{label: '공개', value: 'Y'},{label: '비공개', value: 'N'}]" class="w-150 mr-2" placeholder="모든 상태" prop="access" @change="changePage(1)"/>
          </b-col>
          <b-col cols="4">
            <SearchBlock v-model="listParam" @change="changePage(1)" v-if="false"/>
          </b-col>
        </b-row>
      </div>
      <div class="inner">
        <GridTable :list="list" :paging="paging" @changePage="changePage" v-model:selected-ids="selectedIds" id-field="sq" use-select use-select-all>
          <template v-slot="{ row, no }">
            <GridTableColumn width="80" label="No" class="pointer">{{ no }}</GridTableColumn>
            <GridTableColumn label="프로젝트명" class="text-dark"><a @click.stop="router.push(`/admin/project/article/${row.sq}`)">{{ row.title }}</a>
            </GridTableColumn>
            <GridTableColumn width="120" label="고객사">{{ row.client }}</GridTableColumn>
            <GridTableColumn width="120" label="타입">{{ row.type }}</GridTableColumn>
            <GridTableColumn width="160" label="생성일">{{ formDateTime(row.registerDtt) }}</GridTableColumn>
            <GridTableColumn width="120" label="상태">{{ { 'S': '외부 공유', 'Y': '공개', 'N': '비공개' }[row.access] }}</GridTableColumn>
            <GridTableColumn width="50" label="">
              <b-button variant="ghost" class="p-0" @click="router.push(`/admin/project/article/${row.sq}`)">
                <img class="w-20" src="/admin_image/edit-pencil.svg" alt="수정">
              </b-button>
            </GridTableColumn>
          </template>
          <template v-slot:empty>
            <p class="empty">목록이 없습니다.</p>
          </template>
          <template v-slot:footer-left>
            <b-button variant="danger" @click="remove" v-if="selectedIds.length">삭제</b-button>
          </template>
          <template v-slot:footer-right>
            <b-button variant="primary" class="write-btn ml-10">
              <router-link to="article">새 프로젝트 등록</router-link>
            </b-button>
          </template>
        </GridTable>
      </div>
    </div>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';
[project-list] {
  hr {.mb(20);}
  .inner {.p(0, 40);
    &:first-of-type {.pt(40);}
  }

}
</style>
