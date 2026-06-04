<script setup lang="ts">

import BButton from '@/views/components/common/BButton.vue';

defineProps<{
  info: Record<string, any>
}>();

const cdnHost = import.meta.env.VITE_CDN_HOST;

const download = (url: string) => {
  window.open(url, '_blank');
};
</script>
<template>
  <div fe-download>
    <b-button variant="primary" @click="download(`${cdnHost}/${info.value.location}`)">Download</b-button>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[fe-download] { .mb(40);
  .btn { .rel; .pr(34);
    &:after { .cnt; .abs; .rt(12, 50%); .t-yc; .wh(16); .contain('/image/common/ico-download-w.svg'); }
  }
}

@media (@tl-up) {
  [fe-download] { .mb(60); }
}

@media (@dm-up) {
  [fe-download] { .mb(80); }
}
</style>