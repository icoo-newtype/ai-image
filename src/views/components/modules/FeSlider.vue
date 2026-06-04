<script setup lang="ts">

import CdnImg from '@/views/components/CdnImg.vue';
import { getMediaTypeFromPath } from '@/utils';
import VideoPlayer from '@/views/components/VideoPlayer.vue';
import { computed } from 'vue';

const props = defineProps<{
  info: Record<string, any>
}>();

const data = computed(() => props.info.value.filter(Boolean));
const doubled = computed(() => [...data.value, ...data.value]);

</script>
<template>
  <div fe-slider>
    <ul>
      <li v-for="(row, i) in doubled" :key="`${row}-${i}`">
        <cdn-img :src="row" v-if="getMediaTypeFromPath(row) === 'image'"/>
        <VideoPlayer playsinline loop autoplay fit="cover" :src="row" v-else-if="getMediaTypeFromPath(row) === 'video'"/>
      </li>
    </ul>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[fe-slider] { .pb(120); overflow: hidden;
  img, video { .br(10); }

  ul {
    .flex;
    .items-start;
    gap: 20px;
    animation: slide-left linear infinite;
    animation-duration: 40s;
    width: max-content;
  }

  li {
    .max-w(800);
    > * { .max-w(100%); }
  }
}

@keyframes slide-left {
  0% { transform: translateX(0); }
  100% { transform: translateX(calc(-50% - 10px)); }
}

@media (@ds-up) {
  [fe-slider] { .pb(160); }
}
</style>