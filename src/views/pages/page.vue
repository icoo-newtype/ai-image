<script setup lang="ts">
import { computed, nextTick, provide, ref, watch, watchEffect } from 'vue';
import { useRoute } from 'vue-router';
import { useLoginPageStore } from '@/store/auth';
import PasswordGuard from '@/views/components/PasswordGuard.vue';
import { useBrowserStore } from '@/store/browser.store';
import AppHeader from '@/views/layout/AppHeader.vue';
import AppFooter from '@/views/layout/AppFooter.vue';
import { usePage } from '@/store/page';
import { getComponentsMap } from '@/views/components/modules';
import { useSocialHead } from '@/utils/meta';
import { useState } from '@/store/state';
import { normalizeString, useWindowEvent } from '@/utils';

const route = useRoute();
const state = useState();
const loginStore = useLoginPageStore();
const browserStore = useBrowserStore();
const pageStore = usePage();
const componentsMap = getComponentsMap();
const activeAnchor = ref('');

provide('activeAnchor', activeAnchor);

const project = computed(() => route.params.project as string);
const code = computed(() => route.params.page as string);
const isLogin = computed(() => loginStore.isLogin(project.value));

const article = computed(() => JSON.parse(decodeURIComponent(atob(pageStore.current?.article as string))));

useSocialHead({
  title: pageStore.info?.title,
  image: `${state.host}/${pageStore.info?.ogImage}`,
});

// route 변경 시 현재 페이지 동기화
watchEffect(() => {
  if (code.value) {
    pageStore.setCurrent(code.value);
  }
});

watch(() => route.hash, async (hash) => {
  if (!hash) return;
  const title = decodeURIComponent(hash.replace('#', ''));
  await nextTick();
  await nextTick();

  const titleItem = article.value.find(
      (item: any) => (item.type === 'title' || item.type === 'header') && item.value?.title === title
  );
  if (!titleItem) return;

  // title과 매칭되는 DOM 요소 찾기
  const elements = document.querySelectorAll('article [fe-title-text].title, article [fe-title-text].header');
  const target = Array.from(elements).find(el => {
    const h2 = el.querySelector('p.title');
    return normalizeString(h2?.textContent as string) === normalizeString(title);
  });
  if (!target) return;

  window.scrollTo({ top: (target as HTMLElement).offsetTop, behavior: 'smooth' });
}, { immediate: true });

const scrolled = () => {
  const anchors = document.querySelectorAll('article [fe-title-text].title, article [fe-title-text].header');

  let currentAnchor: Element | null = null;

  anchors.forEach(el => {
    const rect = el.getBoundingClientRect();
    if (rect.top <= 100) {
      currentAnchor = el; // 100px 안에 들어온 것 중 가장 마지막(아래) 것
    }
  });

  activeAnchor.value = currentAnchor
      ? normalizeString((currentAnchor as HTMLElement).querySelector('.title')?.textContent!)
      : '';
};

useWindowEvent('scroll', scrolled);
</script>

<template>
  <password-guard v-if="!isLogin" :project="project"/>
  <div v-else project-page :class="[
      browserStore.scrollDirection,
      browserStore.theme,
      { scrolled: browserStore.scrolled },
      { frozen: browserStore.frozen },
    ]">
    <app-header/>
    <article>
      <div class="content">
        <template v-for="(item, i) in article">
          <component :is="componentsMap[`Fe${item.comp}`]" :info="item" :key="`${item.type}-${i}`" v-if="item.type === 'blank' || item.value"/>
        </template>
      </div>
      <app-footer/>
    </article>
  </div>
</template>

<style lang="less">
@import '~@/less/proj';

ul, ol, dl, h1, h2, h3, h4, h5 { .m; }

[project-page] {
  article { .rel; .mt(60);
    .content { .wf; .crop; .pb(120);
      > div { .ph(20); }
    }
  }
}

@media (@tp-up) {
  [project-page] {
    article {
      .content { .pb(140);
        > div { .ph(30); }
      }
    }
  }
}

@media (@tl-up) {
  [project-page] {
    article { .mt(70); .ml(240);

    }
  }
}

@media (@ds-up) {
  [project-page] {
    article { .ml(300);
      .content { .pb(200);
      }
    }
  }
}

@media (@dm-up) {
  [project-page] {
    article {
      .content {
        > div:not([fe-slider]) { .ph(0); .w(1160); .mh-c; }
      }
    }
  }
}
</style>