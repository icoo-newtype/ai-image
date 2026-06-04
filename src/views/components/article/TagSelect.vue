<script setup lang="ts">
import Drag from '@/views/components/article/Drag.vue';
import { onMounted, ref, watch } from "vue";
import isEmpty from "lodash-es/isEmpty";
import { useToast } from "@/plugins/toast";

const tags = ref<string[]>([]);
const tag = ref<string>();

const props = defineProps(['max','modelValue','required'])
const emit = defineEmits(['update:modelValue'])

const success = useToast({ type: 'success' });
const warning = useToast({ type: 'warning' });

const changeOrder = () => {
  emit('update:modelValue', [...tags.value]);
};

const addTag = () => {
  if (isEmpty(tag.value)) {
    warning('태그를 입력해주세요');
    return;
  }
  if (tag.value && tags.value.includes(tag.value)) {
    warning('이미 등록된 태그입니다.');
    return;
  }
  emit('update:modelValue', [...tags.value, tag.value]);
  tag.value = '';
};
const del = (item: string) => {
  tags.value = tags.value.filter(tag => tag !== item);
  emit('update:modelValue', [...tags.value]);
};
const update = () => {
  tags.value = props.modelValue || [];
};

watch(() => [props.modelValue], update);

onMounted(() => {
  if (props.modelValue) update();
});
</script>

<template>
  <div tag-select>
    <Drag tag="ul" v-model="tags" @end="changeOrder">
      <li v-for="(item, i) in tags" :key="item">
        {{ item }}
        <a class="delete" @click="del(item)"></a>
      </li>
    </Drag>
    <input type="text" placeholder="+ Add Tag" v-model="tag" @keyup.prevent.enter="addTag" class="tag-input"/>
  </div>
</template>

<style lang="less">
@import "~@/less/proj.less";

[tag-select] { .p(12,16,8); background: #F7F7F7;
  ul { .m; .ib;
    li { .vat; .rel; .ib; background: #dcdce1; .fs(12, 18); .p(3,32,3,12); .br(15); .mr(4); .mb(4); cursor: move;
      a { .block; .abs; .rt(6,4); .wh(16); .contain('/admin_image/tag-x.png'); }
    }
  }
  .tag-input { background: #F7F7F7; border: 0; color:#555; outline: 0; .fs(14,24); .ml(12); }
  .tag-input:focus { color:@c-black; }
}
</style>
