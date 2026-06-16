<script setup lang="ts">
import AppHeader from '@/view/layout/AppHeader.vue';
import AppFooter from '@/view/layout/AppFooter.vue';
import { useSocialHead } from '@/utils/meta';
import { useBrowserStore } from '@/stores/browser.store';
import { useI18n } from '@/plugins/i18n';
const { lang } = useI18n();

const { t } = useI18n();
const browserStore = useBrowserStore();

useSocialHead({
  title: t('meta.root.title'),
  description: t('meta.root.description'),
  image: '/image/og.png',
})

</script>

<template>
  <div default-layout :class="[lang, browserStore.scrollDirection, browserStore.theme, { scrolled: browserStore.scrolled }, { frozen: browserStore.frozen }]">
    <app-header />
    <div class="scroll-holder">
      <article class="content-body">
        <router-view v-slot="{ Component }">
          <suspense>
            <keep-alive :include="['project', 'news']">
              <component :is="Component"></component>
            </keep-alive>
            <template #fallback>
              <div>loading ...</div>
            </template>
          </suspense>
        </router-view>
      </article>
    </div>
    <app-footer />
  </div>
</template>

<style lang="less">
@import "~@/less/proj";
[default-layout] { .min-h(100vh);
  .content-body { .rel; z-index: 3; .bgc(#fff); }
}
</style>