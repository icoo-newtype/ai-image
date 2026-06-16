<script setup lang="ts">
import BInputGroup from '@/views/components/common/BInputGroup.vue';
import BInputGroupPrepend from '@/views/components/common/BInputGroupPrepend.vue';
import DropSelect from '@/views/components/form/DropSelect.vue';
import { FilterComponentEmits, FilterComponentProps, ListComponentProps } from '@/types/components';
import { useFilterParams } from '@/hooks/useFilterParam';
import TextInput from '@/views/components/form/TextInput.vue';
import BInputGroupAppend from '@/views/components/common/BInputGroupAppend.vue';
import BButton from '@/views/components/common/BButton.vue';

const props = defineProps<ListComponentProps & FilterComponentProps>()
const emit = defineEmits<FilterComponentEmits>();
const { model, commit } = useFilterParams(props, emit, ['searchType', 'search']);
const clear = () => {
  model.search = '';
  commit();
}
</script>

<template>
  <b-input-group>
    <b-input-group-prepend v-if="list">
      <drop-select style="min-width: 90px" :list="list" :split="split" :obj="obj" v-model="model.searchType" @change="commit"></drop-select>
    </b-input-group-prepend>
    <text-input placeholder="Search" v-model="model.search" @enter="commit"></text-input>
    <b-input-group-append>
      <b-button class="square clear" v-if="model.search" @click="clear"><i class="icon-close"></i></b-button>
      <b-button class="square" variant="dark" @click="commit"><i class="icon-magnifier"></i></b-button>
    </b-input-group-append>
  </b-input-group>
</template>

<style lang="less">
@import '@/less/proj';
[search-block]{
  input{.bgc(#f4f4f4); .-a; .br(3); color:#111;
    &:focus{.bgc(#f4f4f4); color:#111;}
  }
  .btn-light{.-a;
    &:hover{.bgc(#f4f4f4); .-a;}
  }
  .btn-dark{.bgc(#1c1c1c);.-a;
    &:hover{.bgc(#5f5f5f);}
  }
}
</style>