<script setup lang="ts">
import { SortableEvent, VueDraggable } from 'vue-draggable-plus';
import { computed } from 'vue';

const props = defineProps(['list','tag'])
const emits = defineEmits(['update:list','start','end','change','move'])

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
  <VueDraggable drag :animation="150" v-model="list" :tag="tag"
                @start="emits('start')" @end="emits('end')" @change="emits('change')" @move="emits('move')">
    <slot></slot>
  </VueDraggable>
</template>

<style lang="less">
@import "~@/less/proj.less";
[drag] {
}
</style>
