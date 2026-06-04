<script setup lang="ts">

import { enterToBr } from '@/utils';
import VideoPlayer from '@/views/components/VideoPlayer.vue';
import { computed } from 'vue';

const props = defineProps<{
  info: Record<string, any>
}>();

const data = computed(() => props.info.value);
const cdnHost = import.meta.env.VITE_CDN_HOST;

</script>
<template>
  <div fe-video :class="info.type">
    <div class="view">
      <video controls :src="`${cdnHost}/${data.media}`"></video>
      <p class="title" v-html="enterToBr(data.title)" v-if="data.title"></p>
    </div>
    <p class="caption" v-html="enterToBr(data.caption)" v-if="data.caption"></p>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[fe-video] { .mv(10);
  .caption { .m(8, 0, 10); .fs(14, 1.4); .c(#666); }
  video { .f; .wh(100%); .vab; .br(10); object-fit: contain; }
}

@media (@tl-up) {
  [fe-video] {
    .title { .pv(6);
      &:before { .t(6); }
    }
  }
}

@media (@dm-up) {
  [fe-video] {
    .caption { .mt(12); }
  }
}
</style>