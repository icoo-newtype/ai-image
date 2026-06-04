<script lang="ts" setup>
import { computed, inject, nextTick, Ref, ref, watch, watchEffect } from 'vue';
import { useBrowserStore } from '@/store/browser.store';
import CdnImg from '@/views/components/CdnImg.vue';
import { usePage } from '@/store/page';
import DrawerMenuItem from '@/views/components/DrawerMenuItem.vue';
import { useRoute, useRouter } from 'vue-router';
import { normalizeString } from '@/utils';

const browserStore = useBrowserStore();
const pageStore = usePage();

const menuOpen = ref(false);

const roll = computed(() => {
  if (menuOpen.value) return '';
  return browserStore.scrollValue > 100 && browserStore.scrollDirection === 'down' && 'roll';
});

function toggleDrawer() {
  menuOpen.value = !menuOpen.value;
}

const route = useRoute();
const router = useRouter();

const type = computed(() => pageStore.info?.type.toLocaleLowerCase());
const goHome = () => router.push(`/${route.params.project}/`);
const routePage = (code: string) => router.push(`/${route.params.project}/${code}`);

const anchors = computed(() => pageStore.current?.anchors || []);
const activeAnchor = inject<Ref<string>>('activeAnchor');
const isActiveAnchor = ref(false);

function scrollToTitle(title: string) {
  isActiveAnchor.value = false;
  router.push({ ...route, hash: `#${title}` });
}

watch(() => route.fullPath, () => {
  menuOpen.value = false;
});

const openGroups = ref<Set<number>>(new Set(anchors.value.map((_, i) => i)));

watch(anchors, (val) => {
  openGroups.value = new Set(val.map((_, i) => i));
});

function toggleGroup(index: number) {
  if (openGroups.value.has(index)) {
    openGroups.value.delete(index);
  } else {
    openGroups.value.add(index);
  }
}
</script>

<template>
  <div app-header :class="[roll, type]">
    <header>
      <div class="logo">
        <a>
          <cdn-img :src="pageStore.info?.logoImage" :alt="pageStore.info?.title" v-if="pageStore.info?.logoImage"/>
          <h1 v-else>{{ pageStore.info?.title }} </h1>
        </a>
      </div>
      <aside class="drawer" :class="{ on: menuOpen }">
        <div class="drawer-holder">
          <nav class="drawer-menu">
            <ul>
              <!--              <drawer-menu-item v-for="item in pageStore.menuTree" :key="item.code" :item="item" :depth="0"/>-->
              <li v-for="item in pageStore.menuTree" :key="item.code">
                <router-link :to="`/${route.params.project}/${item.code}`">{{ item.label }}</router-link>
              </li>
            </ul>
          </nav>
        </div>
      </aside>
      <a class="btn-hamburger" :class="{ on: menuOpen }" @click="toggleDrawer" v-if="pageStore.menuTree.length > 1"></a>
    </header>
    <div class="anchors" :class="{ 'on': isActiveAnchor }">
      <a class="current" @click="isActiveAnchor = !isActiveAnchor">{{ activeAnchor }}</a>
      <div class="list">
        <ol v-for="(row, groupIndex) in anchors" :key="groupIndex">
          <li v-for="(item, i) in row" :key="i" v-show="!i || openGroups.has(groupIndex)">
            <a @click.prevent="!i ? toggleGroup(groupIndex) : scrollToTitle(item.title)" :class="[item.name.toLowerCase(), { 'on': activeAnchor === normalizeString(item.title) }]">
              {{ item.title }}
            </a>
            <a class="arrow" :class="{ on: openGroups.has(groupIndex) }" v-if="!i" @click.stop="toggleGroup(groupIndex)"></a>
          </li>
        </ol>
      </div>
    </div>
  </div>
</template>

<style lang="less">
@import '~@/less/proj';
.ir { .ir; }

[app-header] { .fix; .lt; .wf; z-index: 49;
  &.roll {
    header { .t-y(-100%); pointer-events: none; }
  }
  &.roll.footerVisible, &.footerVisible {
    header { .t-y(0); }
  }

  header { .rel; .bgc(#fff); .wf; .h(60); .t-y(0); transition: transform 0.6s ease; z-index: 2;
    .logo { .rel; .wh(100%, 60); .p(15, 20); .-b(#E8EAED); .z(2);
      a { .block; }
      img { .block; .h(30); }
      h1 { .fs(18, 30); .bold; }
    }
    .btn-hamburger { .block; .wh(24); .contain('/image/common/ico-hamburger.svg'); .bg-c; .no-repeat; .abs; .rt(20, 18); .z(5);
      &.on { .bg('/image/common/ico-close.svg'); .wh(20); }
    }

  }
  .drawer { .abs; .lt; .wf;
    .drawer-holder { .abs; .rt; .wf; .fvh; .t-x(100%); .bgc(#fff); overflow-y: auto; .-box; transition: transform 0.4s ease;
      .drawer-menu { .p(60, 0, 40);
        ul { .mt(20); }
        li { .p(20); }
        a { .block; .rel; .fs(18, 1.4); .semi-bold; .c(#999);
          &.router-link-active { .c(@c-black); }
        }
      }
    }
    &.on {
      .drawer-holder { .t-x(0); }
    }
  }
  &.essential {
    header {
      img + h1 { .hide; }
    }
    .drawer { .hide; }
  }

  .anchors { .fix; .t(0); .wf; .z(1); .bgc(#fff); .fs(16, 19);
    .current { .block; .rel; .h(59); .p(20); .-box; .medium;
      &:after { .cnt; .abs; .rt(20, 50%); .mt(-7); .wh(14); .contain('/image/common/ico-arrow-down.svg'); }
    }
    .list { .p(0, 20); .-t(#E8EAED); .tr-d(.3s); .crop; .o(0); .h(0); .max-h(calc(100vh - 60px)); overflow-y: auto; }
    ol {
      li {
        a { .block; .pv(12); .medium; .c(#666); .tr-d(0.2s);
          &.header { .c(#000); .semi-bold; }
          &.title { .ph(16); }
          &.arrow { .hide; }
        }
      }
    }

    &.on {
      .current:after { transform: rotate(180deg); }
      .list { .-b(#E8EAED); .pv(12); .o(1); .h(auto); }
    }
  }
}

@media (@tl-up) {
  [app-header] {
    &.roll {
      header { .t-y(0); pointer-events: auto; }
    }
    header { .h(70);
      .logo { .w(auto); .h(70); .p(20, 24); }
      .btn-hamburger { .hide; }
    }
    .drawer { .rt(70, 0); .w(auto); .z(2);
      .drawer-holder { .rel; .f; .t-x(0); background: none;
        .drawer-menu { .p(0);
          ul { .m; .flex; .h(70); .items-center; }
          li { .p; }
          a { .fs(16, 1); }
          li + li { .ml(40); }
        }
      }
    }
    .anchors { .lt(0, 70); .h(calc(100vh - 70px)); .w(240); .-r(#E8EAED);
      .current { .hide; }
      .list { .p(30, 24); .o(1); .h(auto); .max-h(calc(100vh - 70px)); border: 0; }
      ol { .mb(30);
        li { .rel;
          a { .pv(0); .fs(16, 20);
            &.header { .c(#000); .semi-bold; .mb(16); }
            &.arrow { .block; .abs; .rt(0, 50%); .mt(-12); .wh(24); .no-repeat('/image/common/ico-arrow-down.svg'); .bg-c;
              &.on { transform: rotate(180deg); }
            }
            &.title { .p(11, 12); .lh(24); font-weight: 400;
              .no-touch &:not(.on):hover { .bgc(#F9F9F9); }
              &.on { .bgc(#F3F4F6); .br(4); }
            }
          }
        }
      }
    }
    &.essential { .w(auto);
      header { .wh(240, auto); .-r(#E8EAED);
        .logo { .h(auto); }
        img + h1 { .block; .m(12, 0, 0); .medium; .fs(16, 1); }
      }
    }
  }
}

@media (@ds-up) {
  [app-header] { .fix; .lt; .wf; z-index: 49;
    .anchors { .w(300); }
    &.essential {
      header { .w(300); }
    }
  }
}
</style>