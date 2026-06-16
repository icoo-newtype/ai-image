<script setup lang="ts">
import TextInput from '@/views/components/form/TextInput.vue';
import BButton from '@/views/components/common/BButton.vue';
import BForm from '@/views/components/common/BForm.vue';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { setCookie } from '@/utils';
import oax from '@/utils/oax';
import { useState } from '@/store/state';

const state = useState();
const route = useRoute();

const param = ref({ id: '', pwd: '' });
const validator = ref();
const loading = ref(false);
const errorMsg = ref('');

const loginAction = async () => {
  errorMsg.value = '';
  if (!await validator.value?.validate()) return;
  loading.value = true;
  try {
    const { data } = await oax.post<string>('/api/auth/login', param.value);
    setCookie('authToken', data);
    window.location.href = route.query.afterLogin as string || state.afterLogin || '/';
  } catch {
    errorMsg.value = '아이디 또는 비밀번호가 올바르지 않습니다.';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-wrap">
    <div class="login-box">
      <div class="login-header">
        <h1 class="login-title">AI Image</h1>
        <p class="login-subtitle">관리자 로그인</p>
      </div>

      <b-form ref="validator" class="login-form">
        <div class="field-group">
          <label>아이디</label>
          <text-input
            type="text"
            placeholder="아이디를 입력하세요"
            autocomplete="username"
            mandatory
            v-model="param.id"
            title="아이디를 입력해주세요"
            no-message
          />
        </div>

        <div class="field-group">
          <label>비밀번호</label>
          <text-input
            type="password"
            placeholder="비밀번호를 입력하세요"
            autocomplete="current-password"
            mandatory
            v-model="param.pwd"
            @enter="loginAction"
            title="비밀번호를 입력해주세요"
            no-message
          />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <b-button
          variant="primary"
          class="login-btn"
          :disabled="loading"
          @click="loginAction"
        >
          {{ loading ? '로그인 중...' : '로그인' }}
        </b-button>
      </b-form>
    </div>
  </div>
</template>

<style lang="less" scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}

.login-box {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 48px 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #111;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 14px;
  color: #888;
  margin: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;

  label {
    font-size: 13px;
    font-weight: 500;
    color: #444;
  }
}

.error-msg {
  font-size: 13px;
  color: #F86C6B;
  margin: 0;
  text-align: center;
}

.login-btn {
  width: 100%;
  height: 48px;
  margin-top: 8px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
}
</style>
