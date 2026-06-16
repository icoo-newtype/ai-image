<script setup lang="ts">
import oax from '@/utils/oax';
import { useStoredParam } from "@/hooks/useStoredParam";
import { ref } from "vue";
import BRow from "@/views/components/common/BRow.vue";
import BCol from "@/views/components/common/BCol.vue";
import GridTable from "@/views/components/table/GridTable.vue";
import SearchBlock from "@/views/components/form/SearchBlock.vue";
import GridTableColumn from "@/views/components/table/GridTableColumn.vue";
import { formDateTime } from "@/utils/timeUtil";

const listParam = useStoredParam({ status: '0', perPage: 10 });
const list = ref<LoginLogItem[]>([]);
const paging = ref();
const loading = ref(false);
const getList = async () => {
  loading.value = true;
  const { data } = await oax.get<PagingList<LoginLogItem>>('/api/admin/manager/loginLog', listParam.value);
  list.value = data.list;
  paging.value = data.paging;
  loading.value = false;
};

const changePage = (v: number) => {
  listParam.value.pageNo = v;
  getList();
};

getList();
</script>

<template>
  <div login-log class="wrapper">
    <div class="animated fadeIn">
      <div class="inner">
        <h1>{{ $route.name }}</h1>
        <b-row class="mb-50">
          <b-col lg="6"></b-col>
          <b-col lg="6">
            <SearchBlock v-model="listParam" :list="['전체','이름','ID']" @change="getList"/>
          </b-col>
        </b-row>
      </div>
      <div class="inner">
        <GridTable no-select :list="list" :paging="paging" @changePage="changePage">
          <template v-slot="{row, no}">
            <GridTableColumn width="80" label="No">{{ no }}</GridTableColumn>
            <GridTableColumn width="120" label="ID">{{ row.id }}</GridTableColumn>
            <GridTableColumn label="이름" class="black">{{ row.name }}</GridTableColumn>
            <GridTableColumn label="접속일시">{{ formDateTime(row.dtt) }}</GridTableColumn>
            <GridTableColumn label="접속IP">{{ row.ipAddr }}</GridTableColumn>
          </template>
        </GridTable>
      </div>
    </div>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';
[login-log]{
  hr{.mb(20);}
  .inner{.p(0,40);
    &:first-of-type{.pt(40);}
  }

}
</style>
