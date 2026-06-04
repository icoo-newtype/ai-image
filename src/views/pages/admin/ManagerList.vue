<script setup lang="ts">
import BRow from '@/views/components/common/BRow.vue';
import BCol from '@/views/components/common/BCol.vue';
import FilterRadioButton from '@/views/components/form/FilterRadioButton.vue';
import { ref } from 'vue';
import SearchBlock from '@/views/components/form/SearchBlock.vue';
import GridTable from '@/views/components/table/GridTable.vue';
import GridTableColumn from '@/views/components/table/GridTableColumn.vue';
import BButton from '@/views/components/common/BButton.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import Modal from '@/views/components/common/Modal.vue';
import { useStoredParam } from '@/hooks/useStoredParam';
import oax from '@/utils/oax';
import { roles } from '@/data/roles';
import { formDate } from '@/utils/timeUtil';
import BFormGroup from '@/views/components/common/BFormGroup.vue';
import RadioButton from '@/views/components/form/RadioButton.vue';
import BButtonGroup from '@/views/components/common/BButtonGroup.vue';
import BDropdown from '@/views/components/common/BDropdown.vue';
import BDropdownItem from '@/views/components/common/BDropdownItem.vue';
import { useConfirm, useInputDialog } from '@/hooks/useInputDialog';
import { useToast } from '@/plugins/toast';
import BForm from '@/views/components/common/BForm.vue';

const inputDialog = useInputDialog();
const confirm = useConfirm();
const warning = useToast({ type: 'error' });
const success = useToast({ type: 'success' });

const managerStatus = (item: ManagerItem) => {
  if (item.status === 2) return `말소 (${formDate(item.quitDtt)})`;
  if (item.loginAge && item.loginAge > 2) return '정지(장기미접속)';
  if (item.wrongCount > 4) return '정지(5회이상비번오류)';
  return '사용중';
};
const listParam = useStoredParam({ status: '0', perPage: 10 });
const list = ref<ManagerItem[]>([]);
const selectedIds = ref<string[]>([]);
const paging = ref();
const loading = ref(false);
const getList = async () => {
  loading.value = true;
  const { data } = await oax.get<PagingList<ManagerItem>>('/api/admin/manager/list', listParam.value);
  list.value = data.list;
  paging.value = data.paging;
  loading.value = false;
};
const quit = async (id: string | string[]) => {
  const quitReason = await inputDialog('관리자 말소', '말소 사유를 입력해주세요');
  await oax.post('/api/admin/manager/delete', { id, quitReason });
  editOpen.value = false;
  success('말소되었습니다.');
  await getList();
};
const quitSelected = async () => {
  if (!selectedIds.value.length) return warning('선택된 항목이 없습니다');
  await quit(selectedIds.value);
};
const restoreSelected = async () => {
  if (!selectedIds.value.length) return warning('선택된 항목이 없습니다');
  await restore(selectedIds.value);
};

const restore = async (id: string | string[]) => {
  await confirm('정말 복원하시겠습니까?');
  await oax.post('/api/admin/manager/restore', { id });
  success('복원되었습니다.');
  editOpen.value = false;
  await getList();
};

const setNormal = async (id: string) => {
  await confirm('정지를 해제하시겠습니까?');
  await oax.post('/api/admin/manager/release', { id });
  success('정지 해제하였습니다.');
  editOpen.value = false;
  await getList();
};

const register = () => {
  isModify.value = false;
  editInfo.value = { roles: 'MANAGER' };
  editOpen.value = true;
};

const isModify = ref(false);
const editOpen = ref(false);
const editInfo = ref<Partial<ManagerItem>>();
const modify = (item: ManagerItem) => {
  isModify.value = true;
  editInfo.value = { ...item };
  editOpen.value = true;
};

const isManagerItem = (info: Partial<ManagerItem>): info is ManagerItem => !!info.id && !!info.status;

const changePage = (v: number) => {
  listParam.value.pageNo = v;
  getList();
};
const validator = ref();
const save = async () => {
  if (!editInfo.value) return;
  if (!validator.value?.validate()) return;
  await oax.post(`/api/admin/manager/${isModify.value ? 'update' : 'register'}`, editInfo.value);
  success('저장되었습니다');
  editOpen.value = false;
  await getList();
};

const img = ref();

getList();
</script>

<template>
  <div manager class="wrapper">
    <div class="animated fadeIn">
      <div class="inner">
        <h1>{{ $route.name }}</h1>
        <b-row class="mb-50">
          <b-col lg="6">
            <FilterRadioButton v-model="listParam" :obj="{ '0': '전체 계정', '1': '유효 계정', '2': '말소 계정' }" prop="status" @change="getList"/>
          </b-col>
          <b-col lg="6">
            <SearchBlock v-model="listParam" :list="['전체','이름','ID']" @change="getList"/>
          </b-col>
        </b-row>
      </div>
      <div class="inner">
        <GridTable :list="list" :paging="paging" @changePage="changePage" v-model:selected-ids="selectedIds" id-field="id" use-select use-select-all>
          <template v-slot="{ row, no }">
            <GridTableColumn width="80" label="No" class="pointer">{{ no }}</GridTableColumn>
            <GridTableColumn width="120" label="ID">{{ row.id }}</GridTableColumn>
            <GridTableColumn label="이름" class="black">{{ row.name }}</GridTableColumn>
            <GridTableColumn label="말소사유" v-if="listParam.status === '2'">{{ row.quitReason }}</GridTableColumn>
            <GridTableColumn width="120" label="권한" v-else>{{ roles[row.roles] }}</GridTableColumn>
            <GridTableColumn width="120" label="등록일">{{ formDate(row.regDtt) }}</GridTableColumn>
            <GridTableColumn width="120" label="말소일" v-if="listParam.status === '2'">{{ formDate(row.quitDtt) }}</GridTableColumn>
            <template v-else>
              <GridTableColumn width="250" label="상태">{{ managerStatus(row) }}</GridTableColumn>
              <GridTableColumn width="80" label="">
                <b-button variant="ghost" class="p-0" @click="modify(row)">
                  <img class="w-20" src="/admin_image/edit-pencil.svg" alt="">
                </b-button>
              </GridTableColumn>
            </template>
          </template>
          <template #footer-left v-if="selectedIds.length">
            <b-button variant="success" v-if="listParam.status === '2'" @click="restoreSelected">복원</b-button>
            <b-button variant="danger" v-else @click="quitSelected">말소</b-button>
          </template>
          <template #footer-right>
            <b-button variant="primary" @click="register">운영자 등록</b-button>
          </template>
          <template #empty>No list.</template>
        </GridTable>
      </div>
    </div>
    <Modal v-model="editOpen" :title="isModify ? '운영자 수정' : '운영자 등록'">
      <BForm class="register-modal" v-if="editInfo" ref="validator">
        <b-form-group label="아이디" label-for="id" :description="isModify ? '아이디는 변경할 수 없습니다' : null">
          <text-input required id="id" v-model="editInfo.id" :readonly="isModify"></text-input>
        </b-form-group>
        <b-form-group label="비밀번호" label-for="pwd" :description="isModify ? '변경을 원할 경우에만 입력하세요' : '영문, 숫자, 특수문자 조합 8~16자로 입력해주세요'">
          <text-input :required="!isModify" id="pwd" type="password" v-model="editInfo.pwd" :except="editInfo.id" autocomplete="new-password"></text-input>
        </b-form-group>
        <b-form-group label="이름" label-for="name">
          <text-input required id="name" v-model="editInfo.name"></text-input>
        </b-form-group>
        <b-form-group label="권한">
          <RadioButton v-model="editInfo.roles" :obj="roles"/>
        </b-form-group>
        <template v-if="isModify && isManagerItem(editInfo)">
          <b-form-group label="상태">
            <b-button-group>
              <b-button variant="outline" disabled>{{ managerStatus(editInfo) }}</b-button>
              <b-dropdown variant="outline">
                <b-dropdown-item @click="restore(editInfo.id)" v-if="editInfo.status === 2">복원</b-dropdown-item>
                <template v-else>
                  <b-dropdown-item @click="quit(editInfo.id)">말소</b-dropdown-item>
                  <b-dropdown-item @click="setNormal(editInfo.id)" v-if="managerStatus(editInfo) !== '사용중'">정지해제</b-dropdown-item>
                </template>
              </b-dropdown>
            </b-button-group>
          </b-form-group>
        </template>
        <div class="mt-50 tc">
          <b-button class="w-90 mr-1" @click="editOpen = false">취소</b-button>
          <b-button class="w-90" @click="save" variant="primary">확인</b-button>
        </div>
      </BForm>
    </Modal>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';
[manager] {
  hr {.mb(20);}
  .inner {.p(0, 40);
    &:first-of-type {.pt(40);}
  }
}
</style>
