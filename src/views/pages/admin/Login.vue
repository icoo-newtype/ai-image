<script setup lang="ts">
import BRow from '@/views/components/common/BRow.vue';
import BCardGroup from '@/views/components/common/BCardGroup.vue';
import BCardBody from '@/views/components/common/BCardBody.vue';
import BForm from '@/views/components/common/BForm.vue';
import BInputGroup from '@/views/components/common/BInputGroup.vue';
import BInputGroupPrepend from '@/views/components/common/BInputGroupPrepend.vue';
import BInputGroupText from '@/views/components/common/BInputGroupText.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import { ref } from 'vue';
import BButton from '@/views/components/common/BButton.vue';
import { useRoute } from 'vue-router';
import { setCookie } from '@/utils';
import oax from '@/utils/oax';
import { useState } from '@/store/state';

const state = useState();
const param = ref({
  id: '',
  pwd: '',
});
const validator = ref();
const route = useRoute();
const loginAction = async () => {
  if (!await validator.value?.validate()) return;
  const { data } = await oax.post<string>('/api/auth/login', param.value);
  setCookie('authToken', data);
  window.location.href = route.query.afterLogin as string || state.afterLogin || '/admin/';
};
</script>
<template>
  <div class="app flex-row align-items-center">
    <div class="container">
      <b-row class="justify-content-center">
        <div class="w-300">
          <b-card-group>
            <b-card-body>
              <b-form ref="validator">
                <h1 class="tc mb-2"><img src="/admin_image/logo.png" alt=""></h1>
                <p class="text-muted tc mb-15">관리자 계정을 입력해주세요.</p>
                <b-input-group class="mb-2">
                  <b-input-group-prepend>
                    <b-input-group-text class="border-0 h-100"><i class="icon-user"></i></b-input-group-text>
                  </b-input-group-prepend>
                  <text-input required type="text" placeholder="아이디" autocomplete="username" mandatory v-model="param.id" title="아이디를 입력해주세요" no-message/>
                </b-input-group>
                <b-input-group class="mb-4">
                  <b-input-group-prepend>
                    <b-input-group-text class="border-0 h-100"><i class="icon-lock"></i></b-input-group-text>
                  </b-input-group-prepend>
                  <text-input required type="password" placeholder="비밀번호" autocomplete="current-password" mandatory v-model="param.pwd" @enter="loginAction" no-message/>
                </b-input-group>
                <b-row class="justify-content-center mt-30">
                  <b-button variant="primary" class="px-4 w-260" @click="loginAction">로그인</b-button>
                </b-row>
              </b-form>
            </b-card-body>
          </b-card-group>
        </div>
      </b-row>
    </div>
  </div>
</template>