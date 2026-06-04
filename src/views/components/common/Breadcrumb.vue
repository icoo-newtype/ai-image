<script setup lang="ts">
import { useRoute } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();
const filtered = computed(() => route.matched.filter(route => route.name || route.meta.label))
</script>
<template>
  <ol class="breadcrumb">
    <li v-for="(m, i) in filtered" class="breadcrumb-item">
      <span v-if="i === filtered.length - 1" class="active">{{ m.name || m.meta.label }}</span>
      <router-link v-else :to="{ name: m.name }">{{ m.name || m.meta.label }}</router-link>
    </li>
  </ol>
</template>