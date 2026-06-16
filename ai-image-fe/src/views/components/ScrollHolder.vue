<script setup lang="ts">
import { nextTick, onMounted, onUpdated, ref } from 'vue';
import { useWindowResize } from '@/hooks/useWindowResize';

const props = defineProps<{
  stop?: boolean,
  innerThumb?: boolean,
  thinThumb?: boolean,
  darkThumb?: boolean,
  maxHeight?: number,
  minHeight?: number,
  large?: boolean,
}>();
const emits = defineEmits(['scroll', 'bottom']);
let downY: number;
let startY: number;
let thumbY: number;
let thumbMax: number;
const thumb = ref<HTMLElement>();
const scrollArea = ref<HTMLElement>();
const maxScroll = ref(0);
const scrollValue = ref(0);
const scrollUpdate = () => {
  if (!scrollArea.value) return;
  if (props.maxHeight && scrollArea.value.offsetHeight > props.maxHeight) {
    scrollArea.value.style.height = `${props.maxHeight}px`;
  }
  if (props.minHeight) {
    scrollArea.value.style.minHeight = `${props.minHeight}px`;
  }
  scrollValue.value = scrollArea.value.scrollTop;
  maxScroll.value = scrollArea.value.scrollHeight - scrollArea.value.offsetHeight;
  const sr = scrollValue.value / maxScroll.value;
  const hr = scrollArea.value.offsetHeight / scrollArea.value.scrollHeight;
  const trackHeight = scrollArea.value.offsetHeight - 4;
  const thumbHeight = Math.max(trackHeight * hr, 20);
  thumbMax = trackHeight - thumbHeight;
  emits('scroll');
  if (maxScroll.value > 0 && scrollValue.value >= maxScroll.value) emits('bottom');
  if (!thumb.value) return;
  thumb.value.style.height = thumbHeight + 'px';
  thumbY = thumbMax * sr;
  thumb.value.style.transform = `translateY(${thumbY}px)`;
};
const el = ref();
defineExpose({
  top: () => {
    scrollArea.value?.scrollTo(0, 0);
  },
  bottom: () => {
    scrollArea.value?.scrollTo(0, maxScroll.value);
  },
  isBottom: () => scrollValue.value >= maxScroll.value,
  el,
});

const thumbDown = (e: MouseEvent) => {
  downY = e.screenY;
  startY = thumbY;
  document.addEventListener('mousemove', thumbMove);
  document.addEventListener('mouseup', thumbUp);
};
const thumbMove = (e: MouseEvent) => {
  const d = e.screenY - downY;
  let ty = startY + d;
  if (ty < 0) ty = 0;
  if (ty > thumbMax) ty = thumbMax;
  const r = ty / thumbMax;
  scrollArea.value!.scrollTop = maxScroll.value * r;
};
const thumbUp = () => {
  document.removeEventListener('mousemove', thumbMove);
  document.removeEventListener('mouseup', thumbUp);
};

onUpdated(scrollUpdate);
useWindowResize(scrollUpdate);
onMounted(async () => {
  scrollArea.value?.addEventListener('wheel', (e: WheelEvent) => {
    if (props.stop) e.preventDefault();
  });
  scrollArea.value?.addEventListener('scroll', scrollUpdate);
  thumb.value?.addEventListener('mousedown', thumbDown);
  scrollUpdate();
  await nextTick();
  scrollUpdate();
});

</script>
<template>
  <div scroll-holder :class="{ 'has-up': scrollValue > 0, 'has-down': scrollValue < maxScroll, 'inner-thumb': innerThumb, 'dark-thumb': darkThumb, 'thin-thumb': thinThumb,  large }" :style="maxHeight ? `max-height: ${maxHeight}px` : ''" ref="el">
    <div class="scroll-area" ref="scrollArea">
      <slot/>
    </div>
    <div class="thumb" ref="thumb" v-show="maxScroll > 0"></div>
  </div>
</template>
<style lang="less">
@import "@/less/proj";
[scroll-holder] { .pr(14); .mr(-14); .rel; .flex; .flex-column;
  &.large { .pr(16); .mr(-16); }
  > .thumb { .w(8); .br(4); .bgc(#EAEAEF); .abs; .rt(0, 2); .pointer; }
  .scroll-area { .auto-y; .flex(1);
    &::-webkit-scrollbar { .wh(0); }
  }
  &.dark-thumb {
    > .thumb { .bgc(#D5D5DE); }
  }
  &.has-up:before { .cnt; .events-none; .wh(calc(100% - 14px), 10); .up-down-shadow; .abs; .lt; .z(1); }
  &.has-down:after { .cnt; .events-none; .wh(calc(100% - 14px), 10); .down-up-shadow; .abs; .lb; .z(1); }
  &.inner-thumb { .pr(0); .mr(0);
    > .thumb { .w(4); .bgc(rgba(0,0,0,0.15)); .rt(2, 2); }
    &.large > .thumb { .w(8); .bgc(rgba(0,0,0,0.15)); .rt(4, 2); }
    &.has-up:before { .wf; }
    &.has-down:after { .wf; }
  }
  &.thin-thumb { .pr(10); .mr(-10);
    > .thumb { .w(6); .bgc(rgba(0,0,0,0.15)); .rt(1, 2); }
    &.has-up:before { .wf; }
    &.has-down:after { .wf; }
  }
}
</style>
