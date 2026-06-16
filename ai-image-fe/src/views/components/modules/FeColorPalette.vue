<script setup lang="ts">

import { computed, ref } from 'vue';
import { enterToBr, useWindowEvent } from '@/utils';

const props = defineProps<{
  info: Record<string, any>
}>();


const data = computed(() => props.info.value);
const length = computed(() => Number(props.info.type?.match(/\d+/)?.[0] ?? 0));

const copiedIndex = ref<number | null>(null);
const tooltipPos = ref({ x: 0, y: 0 });
const windowWidth = ref(window.innerWidth);
const isMobile = computed(() => window.innerWidth < 1440);

useWindowEvent('resize', () => windowWidth.value = window.innerWidth);

function onMouseMove(e: MouseEvent) {
  if (isMobile.value) return;
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
  tooltipPos.value = { x: e.clientX - rect.left, y: e.clientY - rect.top };
}

async function copyHex(color: string, index: number) {
  await navigator.clipboard.writeText(`#${color}`);
  copiedIndex.value = index;
  setTimeout(() => {
    copiedIndex.value = null;
  }, 1500);
}

function getViewStyle(color: string) {
  const isWhite = ['FFF', 'FFFFFF'].includes(color.toUpperCase());
  return {
    backgroundColor: `#${color}`,
    borderColor: isWhite ? '#E8EAED' : `#${color}`,
  };
}
</script>
<template>
  <div fe-color-palette>
    <p class="title" v-if="data.title">{{ data.title }}</p>
    <ul :class="`column-${length}`">
      <li v-for="(row, i) in data.colors" :key="i">
        <template v-if="row.color">
          <div class="view" :style="getViewStyle(row.color)" @mousemove="onMouseMove" @click="copyHex(row.color, i)">
            <span class="tooltip" :class="{ copied: copiedIndex === i }" :style="isMobile ? {} : { left: `${tooltipPos.x}px`, top: `${tooltipPos.y}px` }" :key="`p${i}`">
              <em class="copy-text">Copy HEX</em>
              <em class="copied-text">HEX Copied!</em>
            </span>
          </div>
          <div class="text">
            <p class="sub-title">{{ row.subTitle || '&nbsp' }}</p>
            <p>#{{ row.color }}</p>
            <p v-html="enterToBr(row.appendix)"></p>
          </div>
        </template>
      </li>
    </ul>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

[fe-color-palette] {
  .title { .fs(16, 1.4); .semi-bold; .mb(20); }
  ul { .grid(2); grid-column-gap: 10px; grid-row-gap: 16px;
    li { .rel; .br(10); .-a(#E8EAED); .crop;
      .view { .abs; .lt(-1); .r(-1); .t(-1); .h(120); cursor: pointer; .-a(#E8EAED);
        .tooltip { .abs; .lt(50%, 50%); .p(6, 10); transform: translate(-50%, -50%);
          background: rgba(255, 255, 255, 0.9); .fs(14, 1); .br(4); .medium;
          white-space: nowrap; pointer-events: none;
          opacity: 0;
          &.copied { opacity: 1; }
          .copy-text { display: block; }
          .copied-text { display: none; }
          &.copied {
            .copy-text { display: none; }
            .copied-text { display: block; }
          }
          .copy-text { display: none; }
        }
      }
      .text { .p(136, 16, 16); }
      .sub-title { .fs(14, 1.4); .semi-bold; .mb(8); }
      p:not(.sub-title) { .fs(11, 1.4); .c(#666); }
    }
  }

  & + & { .mt(16); }
}

@media (@tp-up) {
  [fe-color-palette] {
    ul {
      &.column-2 { .grid(2, 16); }
      &.column-4 { .grid(4, 16); }
    }
  }
}

@media (@ds-up) {
  [fe-color-palette] {
    ul li {
      .view { cursor: pointer;
        .tooltip { left: 0; top: 0; transform: translate(10px, 10px); opacity: 0;
          .copy-text { display: block; }
        }
        &:hover .tooltip { opacity: 1; }
      }
    }
  }
}

@media (@dm-up) {
  [fe-color-palette] {
    .title { .fs(18, 1.4); }
    ul {
      &.column-2 { .grid(2, 20); }
      &.column-4 { .grid(4, 20); }
      li {
        .view { .h(160); }
        .text { .p(180, 20, 20); }
        .sub-title { .fs(16, 1.4); }
        p:not(.sub-title) { .fs(12, 1.4); }
      }
    }
    & + & { .mt(20); }
  }
}
</style>