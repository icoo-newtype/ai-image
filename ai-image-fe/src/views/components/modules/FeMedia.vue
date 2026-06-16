<script setup lang="ts">

import CdnImg from '@/views/components/CdnImg.vue';
import { enterToBr, getMediaTypeFromPath } from '@/utils';
import VideoPlayer from '@/views/components/VideoPlayer.vue';
import { computed } from 'vue';

const props = defineProps<{
  info: Record<string, any>
}>();

const data = computed(() => props.info.value);

</script>
<template>
  <div fe-media :class="info.type">
    <ul :class="`column-${((data.length - 1) % 3) + 1}`">
      <li v-for="(row, i) in data" :key="i">
        <div class="view">
          <cdn-img :src="row.media" v-if="getMediaTypeFromPath(row.media) === 'image'"/>
          <VideoPlayer playsinline loop autoplay fit="cover" :src="row.media" v-else-if="getMediaTypeFromPath(row.media) === 'video'"/>
          <p class="title" v-html="enterToBr(row.title)" v-if="row.title"></p>
        </div>
        <p class="caption" v-html="enterToBr(row.caption)" v-if="row.caption"></p>
      </li>
    </ul>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[fe-media] {
  img { .max-w(100%); .wf; }
  li { .p(10, 0);
    .caption { .m(8, 0, 10); .fs(14, 1.4); .c(#666); }
  }

  &.basic {
    img, video { .br(10); }
  }

  &.incorrect, &.correct {
    .view { .rel; .br(10); .crop; .pb(28); }
    .title { .abs; .lb; .wf; .p(4, 12, 4, 36); .-box; .fs(14, 20);
      &:before { .cnt; .bg('/image/common/ico-x.svg'); .wh(20); .abs; .lt(12, 4); }
    }
    .caption { .ph(12); }
  }
  &.incorrect .title { .bgc(#FF6140); }
  &.correct .title { .bgc(#00BF40); }
}

@media (@tp-up) {
  [fe-media] {
    ul {
      &.column-2 { .grid(2); grid-column-gap: 20px; }
      &.column-3 { .grid(2); grid-column-gap: 20px; }
    }
  }
}

@media (@tl-up) {
  [fe-media] {
    &.incorrect, &.correct {
      .view { .pb(32); }
      .title { .pv(6);
        &:before { .t(6); }
      }
    }
  }
}

@media (@dm-up) {
  [fe-media] {
    li {
      .caption { .mt(12); }
    }
    ul {
      &.column-3 { .grid(3); grid-column-gap: 20px; }
    }
  }
}
</style>