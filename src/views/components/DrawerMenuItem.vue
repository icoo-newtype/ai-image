<script lang="ts" setup>
import { computed, inject, provide, Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { PageItem, usePage } from '@/store/page';
import { ModalItem } from '@/plugins/modal/types';
import { ModuleItem } from '@/types/components';
import { normalizeString } from '@/utils';

const props = defineProps<{ item: PageItem; depth?: number }>();
const depth = props.depth ?? 0;

const route = useRoute();
const router = useRouter();
const page = usePage();
const hasChildren = !!props.item.children?.length;

const pageCode = computed(() => props.item.code.replace(`${props.item.parentCode}-`, ''));

const open = computed(() =>
    route.params.category === props.item.code ||
    props.item.children?.some(child => child.code === `${route.params.category}-${route.params.page}`)
);

const active = computed(() => open.value || route.params.page === pageCode.value);


const anchors = computed(() => {
  if (depth < 1) return [];
  return page.current?.anchors;
});

function navigate() {
  if (hasChildren) {
    const first = props.item.children![0];
    const firstPageCode = first.code.replace(`${first.parentCode}-`, '');
    router.push(`/${route.params.project}/${props.item.code}/${firstPageCode}`);
  } else {
    router.push(`/${route.params.project}/${props.item.parentCode}/${pageCode.value}`);
  }
}

function scrollToTitle(title: string) {
  router.push({ ...route, hash: `#${title}` });
}

const activeAnchor = inject<Ref<string>>('activeAnchor');
</script>

<template>
  <li>
    <a href="#" :class="{ on: active, hide: !depth && page.info?.type === 'Essential' }" @click.prevent="navigate">{{ item.label }}</a>
    <template v-if="hasChildren && open">
      <ul>
        <drawer-menu-item v-for="child in item.children" :key="child.code" :item="child" :depth="depth + 1"/>
      </ul>
    </template>

    <ol v-if="depth >= 1 && active && anchors?.length">
      <li v-for="row in anchors" :key="row.name">
        <a href="#" @click.prevent="scrollToTitle(row.title)" :class="{ 'on': activeAnchor === normalizeString(row.title) }">
          {{ row.title }}
        </a>
      </li>
    </ol>
  </li>
</template>