<script setup lang="ts">
import BCardBody from '@/views/components/common/BCardBody.vue';
defineProps<{ header?: string }>()
</script>
<template>
  <div class="card">
    <div class="card-header" v-if="header">{{ header }}</div>
    <b-card-body><slot /></b-card-body>
  </div>
</template>