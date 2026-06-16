<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{ paging: Paging }>();
defineEmits<{ changePage: [number] }>();
const totalPage = computed(() => Math.ceil(props.paging.listCount / props.paging.perPage));
const firstPage = computed(() => Math.max(Math.min(props.paging.pageNo - 2, totalPage.value - 4), 1));
const lastPage = computed(() => Math.min(firstPage.value + 4, totalPage.value));
</script>
<template>
  <ul class="pagination b-pagination">
    <li class="page-item" :class="{ disabled: paging.pageNo === 1 }" @click="$emit('changePage', 1)"><span class="page-link"><img src="/admin_image/first-arrow.png" class="w-20" alt="<<"></span></li>
    <li class="page-item" :class="{ disabled: paging.pageNo === 1 }" @click="$emit('changePage', paging.pageNo - 1)"><span class="page-link"><img src="/admin_image/prev-arrow.png" class="w-20" alt="<"></span></li>
    <template v-for="i in 5">
      <li class="page-item" :class="{ active: i + firstPage - 1 === paging.pageNo }" v-if="(i + firstPage - 1) === 1 || (i + firstPage - 1) === totalPage || (i > 1 && (i + firstPage - 1) < lastPage)">
        <button type="button" class="page-link" @click="$emit('changePage', i + firstPage - 1)">{{ i + firstPage - 1 }}</button>
      </li>
      <li class="page-item disabled bv-d-xs-down-none" v-else-if="i + firstPage - 1 >= 1 && i + firstPage - 1 <= totalPage"><span class="page-link">…</span></li>
    </template>
    <li class="page-item" :class="{ disabled: paging.pageNo === totalPage }" @click="$emit('changePage', paging.pageNo + 1)"><span class="page-link"><img src="/admin_image/next-arrow.png" class="w-20" alt=">"></span></li>
    <li class="page-item" :class="{ disabled: paging.pageNo === totalPage }" @click="$emit('changePage', totalPage)"><span class="page-link"><img src="/admin_image/last-arrow.png" class="w-20" alt=">>"></span></li>
  </ul>
</template>
<style lang="less">
@import "@/less/proj";
.b-pagination {
  .page-item.disabled { .events-none; }
}
</style>