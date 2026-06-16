<script lang="ts" setup>
import { usePage } from '@/store/page';
import CdnImg from '@/views/components/CdnImg.vue';
import { computed } from 'vue';

const cdnHost = import.meta.env.VITE_CDN_HOST;
const pageStore = usePage();

const resource = computed(() => {
  return pageStore.info?.resource ?? [];
});

const goTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};
</script>
<template>
  <footer app-footer>
    <a class="btn-top ir" @click="goTop">TOP</a>
    <div class="split-warp">
      <div class="logo">
        <cdn-img :src="pageStore.info?.footerLogoImage" :alt="pageStore.info?.title" v-if="pageStore.info?.footerLogoImage"/>
      </div>
      <div class="resource">
        <p>Resource</p>
        <ul>
          <li v-for="row in resource as FileItem[]" :key="row.sq">
            <a target="_blank" :href="`${cdnHost}/${row.location}`">{{ row.fileName }}</a>
          </li>
        </ul>
      </div>
    </div>
    <p class="copyright">Design Kit is a design asset service by <a href="https://newtype.design" target="_blank">Newtype Imageworks</a></p>
  </footer>
</template>
<style lang="less">
@import '~@/less/proj';

[app-footer] { .rel; .p(40, 20, 50); .bgc(#000); .c(#fff);
  .btn-top { .block; .abs; .rt(20, -62); .wh(32); .contain('/image/common/ico-top.svg'); }
  .logo {
    img { .block; .max-w(100%); }
  }
  .resource { .mt(80);
    p { .fs(20); .semi-bold; .mb(30); }
    ul { .-b(rgba(255, 255, 255, 0.20));
      li { .rel; .-t(rgba(255, 255, 255, 0.20)); .pv(8);
        &:after { .cnt; .abs; .rt(4, 50%); .t-yc; .wh(16); .contain('/image/common/ico-download-w.svg'); }
      }
    }
  }
  .copyright { .mt(120); .-t(#333); .pt(20); .c(#808080);
    .no-touch & a:hover { .c(#fff); }
  }
}

@media (@tp-up) {
  [app-footer] { .p(70, 30, 50);
    .logo img { .max-w(95%); .max-h(50); }
    .btn-top { .r(30); }
    .split-warp { .grid(2); }
    .resource { .mt(0); }
    .copyright { .mt(120); .fs(14, 1.3); }
  }
}

@media (@tl-up) {
  [app-footer] { .p(70, 30, 50); }
}

@media (@dm-up) {
  [app-footer] { .p(70, 70, 50);
    .btn-top { .rt(70, -70); .wh(40); }
    .resource {
      p { .fs(24); }
    }
  }
}
</style>
