<script setup lang="ts">

import { ref, watchEffect } from 'vue';
import BButton from '@/views/components/common/BButton.vue';
import { useConfirmCancel } from '@/hooks/useInputDialog';
import { getComponentsMap } from '@/views/components/modules';

const props = defineProps(['modelValue', 'required', 'idx']);
const emit = defineEmits(['update:modelValue', 'insertBefore', 'insertAfter', 'copy', 'remove', 'up', 'down']);

const data = ref<any>(props.modelValue || {});
const componentsMap = getComponentsMap();
const confirm = useConfirmCancel();
const remove = async () => {
  await confirm('해당 모듈을 삭제하시겠습니까?');
  emit('remove');
};

watchEffect(() => data.value = props.modelValue || []);
</script>
<template>
  <div module-item :key="`module-${idx}`" :class="{ 'justify-content-end': data.type === 'body-text' && data.filekey === 'project'}">
    <div class="focus">
      <a class="handle up" @click="$emit('up')"></a>
      <a class="handle down" @click="$emit('down')"></a>
      <b-button variant="primary" class="top" @click="$emit('insertBefore')">ADD MODULE</b-button>
      <b-button variant="primary" class="bottom" @click="$emit('insertAfter')">ADD MODULE</b-button>
      <div class="tag">
        <div class="inner">
          <p>{{ data.name }}</p>
          <span></span>
          <button @click="$emit('copy')"><img src="/admin_image/copy-paste-icon.png" alt=""></button>
          <button class="delete" @click="remove"><img src="/admin_image/trash-icon.png" alt=""></button>
        </div>
      </div>
    </div>
    <component :is="componentsMap[data.comp]" :type="data.type" v-model="data.value" :idx="idx"/>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';


[module-item] { .rel; .-a(transparent, 1); .bgc(#fff);
  .dragging & {
    .handle, .top, .bottom, .tag {.hide;}
    &:hover {.-a(transparent, 1px);
      .handle, .top, .bottom, .tag {.hide;}
    }
    &[draggable='true'] { .bgc(rgba(0, 0, 0, 0.05)); }
  }

  &:hover {.-a(#1c1c1c, 1px);
    .handle, .top, .bottom, .tag { .block; z-index: 2 }
  }
  .handle { .block; .abs; .lt; .bgc(#1C1C1C); cursor: pointer; .wh(30);
    &:after { .cnt; .abs; .lt(50%, 50%); .ml(-7); .mt(-7); .wh(14); .bg('/image/common/ico-arrow-down-w.svg'); .t-rev-v(); }
    &.down { .t(31);
      &:after { transform: none; }
    }
  }
  .top, .bottom { .abs; .l(50%); .fs(12); .p(8, 24); .br-b(4); .bold; }
  .top { .br-t(4); transform: translate(-50%, -100%); }
  .bottom {.br-b(4); .b(0); transform: translate(-50%, 100%)}
  .tag {.abs; .rt(6, 6); .pb(5); .bgc(#fff);
    .inner {box-shadow: 0 2px 13px 0 rgba(0, 0, 0, 0.15);.p(4, 8, 5, 12);
      p {.fs(12); color: #1c1c1c; .ib; .m(0); .bold; cursor: default;}
      span {.w(1); .h(14); .bgc(#ddd); .ib; .ml(8); .vam;}
      button {.bgc(transparent); .wh(20, 20); .vab; .ml(8); .ib;
        img {.wf; .block;}
        &.delete { .ib; .vam; cursor: pointer; }
      }
    }
  }
  .handle, .top, .bottom, .tag {.hide;}
}

</style>