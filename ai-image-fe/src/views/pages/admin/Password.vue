<script setup lang="ts">
import BCard from '@/views/components/common/BCard.vue';
import BFormGroup from '@/views/components/common/BFormGroup.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import { ref } from 'vue';
import { useAuthInfo } from '@/store/auth';
import BButton from '@/views/components/common/BButton.vue';
import BForm from '@/views/components/common/BForm.vue';
import oax from '@/utils/oax';

defineProps<{ type?: string }>();
const authInfo = useAuthInfo();
const validator = ref();
const pwdRepeat = ref('');
const param = ref({
  pwd: '',
  newPwd: '',
});

const change = async () => {
  if (!await validator.value?.validate()) return;

  await oax.post('/api/admin/changePwd', param.value);
  alert('변경되었습니다');
  window.location.href = '/admin';
};
</script>
<template>
  <article password>
    <BForm class="w-700" ref="validator">
      <h1 class="tc mb-0"><img src="/admin_image/logo.png" alt="logo"></h1>
      <h2 class="tc">비밀번호를 변경해 주세요!</h2>
      <b-card class="mt-40">
        <b-form-group :label-cols="4" horizontal label="현재 비밀번호" label-for="pwd">
          <text-input id="pwd" v-model="param.pwd" required type="password"/>
        </b-form-group>
        <b-form-group :label-cols="4" horizontal label="새 비밀번호" label-for="newPwd">
          <text-input id="newPwd" v-model="param.newPwd" pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[\]\[\{\}\|\(\)\/\-`~!@#$%^&*_+=,.<>?:;'&quot;]).{8,16}$" autocomplete="new-password" required type="password" mismatch-message="영문, 숫자, 특수문자 조합 8~16자로 입력해주세요" info-message="영문, 숫자, 특수문자 조합 8~16자로 입력해주세요"/>
        </b-form-group>
        <b-form-group :label-cols="4" class="mb-0" horizontal label="새 비밀번호 확인" label-for="repeat">
          <text-input id="repeat" v-model="pwdRepeat" autocomplete="new-password" required type="password" :pattern="param.newPwd" mismatch-message="비밀번호가 일치하지 않습니다."/>
        </b-form-group>
      </b-card>
      <div class="tc">
        <b-button v-if="type !== 'my'" class="px-4" to="/admin/" variant="outline-secondary">다음에 변경하기</b-button>
        <b-button class="px-4" variant="primary" @click="change">비밀번호 변경</b-button>
      </div>
    </BForm>
  </article>
</template>

<style lang="less">
@import '@/less/proj.less';
[password] { .h(calc(100vh - 48px)); .flex; .flex-middle; .items-center;
  .card { .-a(#E7E7E7); }
}
</style>