<script setup lang="ts">

import { computed } from "vue";
import Drag from "@/views/components/article/Drag.vue";

const props = defineProps(['list'])
const emits = defineEmits(['update:list'])

const list = computed({
  get(){
    return props.list;
  },
  set(value){
    emits('update:list', value);
  },
});
</script>

<template>
  <div draggable-table grid-table>
    <table>
      <thead>
      <tr>
        <th style="width: 50px"></th>
        <th style="width: 80px">No</th>
        <slot name="thead"></slot>
      </tr>
      </thead>
      <Drag tag="tbody" v-model:list="list" handle=".handle">
        <tr class="list-group-item" v-for="(element,i) in list" :key="i">
          <td class="text-center">
            <i class="bi-list handle"></i>
          </td>
          <td>{{ i+1 }}</td>
          <slot :row="element" :i="i"></slot>
        </tr>
      </Drag>
    </table>
  </div>
</template>

<style lang="less">
@import "~@/less/proj.less";
[draggable-table] {
  tbody {
    .list-group-item { display: table-row; .-a;}
  }
}
</style>
