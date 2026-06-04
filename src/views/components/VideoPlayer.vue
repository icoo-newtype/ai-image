<script setup lang="ts">
import { onMounted, ref } from 'vue';

const props = defineProps({
  src: { type: String },
  autoplay: {type: Boolean, default: false },
  loop: {type: Boolean, default: false },
  muted: {type: Boolean, default: true },
  playsinline: {type: Boolean, default: false },
  fit: { type: String, default: 'contain' },
  max: { type: Number, default: -1 },
  controls:  { type: Boolean, default: false }
});
const emit = defineEmits(['ended', 'updated']);
const video = ref<HTMLVideoElement>();
const isPaused = ref(false);
const isMuted = ref(false);

const mute = (val: boolean) => {
  isMuted.value = val;
  if (video.value) video.value.muted = val;
};
const getMuted = () => {
  return isMuted.value;
};

const play = () => {
  isPaused.value = false;
  video.value?.play();
};

const pause = () => {
  isPaused.value = true;
  video.value?.pause();
};

const togglePlay = () => {
  if (isPaused.value) play();
  else pause();
};

const toggleMute = () => {
  mute(!getMuted());
};

const updated = (e: Event) => {
  const target = e.target as HTMLVideoElement;
  if (props.max > -1 && target.currentTime >= props.max) {
    emit('ended');
  } else {
    emit('updated', (target.currentTime * 100) / Math.min(target.duration, props.max));
  }
};

const cdnHost = import.meta.env.VITE_CDN_HOST;

onMounted(() => {
  if (typeof window === 'undefined') return;
  isPaused.value = !props.autoplay;
  isMuted.value = props.muted;
});
</script>
<template>
  <div video-player>
    <video ref="video" :class="fit" :autoplay="autoplay" :loop="loop" :muted="muted" :playsinline="playsinline" :src="`${cdnHost}/${src}`" @timeupdate="updated" @ended="$emit('ended')"></video>
    <button v-if="controls" class="control video-mute-btn" :class="{ 'video-mute': isMuted }" @click.stop="toggleMute"></button>
    <button v-if="controls" class="control video-control-btn" :class="{ 'video-control-btn-stop': isPaused }" @click.stop="togglePlay"></button>
  </div>
</template>
<style lang="less">
@import "~@/less/proj.less";
[video-player] { .rel; .f;
  video { .f; .wh(100%); .vab;
    &.cover { object-fit: cover; }
    &.contain { object-fit: contain; }
  }
  .control { .abs; .wh(40, 40); background-color: transparent; .cover; .bg-c; .no-repeat; z-index: 6; }
  .video-control-btn { .rb(20, 20); }
  .video-mute-btn { .rb(74, 20); }
  .video-control-btn { .bg('/admin_image/common/video-pause.png');
    &:hover { .bg('/admin_image/common/video-pause-hover.png');}
  }
  .video-control-btn-stop { .bg('/admin_image/common/video-play.png');
    &:hover {.bg('/admin_image/common/video-play-hover.png');}
  }
  .video-mute-btn { .bg('/admin_image/common/video-mute.png');
    &:hover {.bg('/admin_image/common/video-mute-hover.png');}
  }
  .video-mute { .bg('/admin_image/common/video-sound.png');
    &:hover {.bg('/admin_image/common/video-sound-hover.png');}
  }
}
</style>
