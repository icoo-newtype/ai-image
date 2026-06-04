<script setup lang="ts">
import { ref } from 'vue';
import BButton from '@/views/components/common/BButton.vue';

defineProps<{ text?: string, dropup?: boolean, dropend?: boolean, variant?: Variant, dir?: string }>();
const show = ref(false);
const el = ref<HTMLElement>();
const toggle = () => {
  if (!show.value) {
    show.value = true;
    document.addEventListener('mousedown', (e) => {
      if (el.value?.contains(e.target as HTMLElement)) {
        document.addEventListener('mouseup', () => {
          show.value = false;
        }, { once: true });
      } else {
        show.value = false;
      }
    }, { once: true, capture: true });
  }
};
</script>
<template>
  <div :dir="dir" class="dropdown b-dropdown btn-group" :class="[{ dropup, dropend, show }, `dropdown-${variant ?? 'default'}`]" ref="el">
    <b-button type="button" class="btn dropdown-toggle" v-html="text" @mousedown="toggle" :variant="variant"></b-button>
    <ul tabindex="-1" class="dropdown-menu" :class="{ show }">
      <slot/>
    </ul>
  </div>
</template>
<style lang="less">
@import '@/less/proj.less';

</style>