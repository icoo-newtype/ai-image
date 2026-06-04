<script setup lang="ts">
import { ref } from 'vue';
import oax from '@/utils/oax';
import { useLoginPageStore } from '@/store/auth';
import { useToast } from '@/plugins/toast';
import TextInput from '@/views/components/form/TextInput.vue';
import BButton from '@/views/components/common/BButton.vue';
import BForm from '@/views/components/common/BForm.vue';

const props = defineProps<{
  project: string;
}>();

const pageStore = useLoginPageStore();
const warning = useToast({ type: 'error' });
const password = ref('');
const validator = ref();

const checkPassword = async () => {
  if (!await validator.value?.validate()) return;

  try {
    const { data: isMatched } = await oax.post<boolean>('/api/project/checkPassword', {
      slug: props.project,
      password: password.value
    });

    if (isMatched) {
      pageStore.setLoginPage(props.project);
    } else {
      warning('비밀번호가 일치하지 않습니다.');
      password.value = '';
    }
  } catch (e) {
    warning('인증 오류가 발생했습니다.');
  }
};
</script>

<template>
  <div class="password-form">
    <b-form ref="validator">
      <div class="logo"></div>
      <p>Enter password to access the site.</p>
      <text-input required type="password" placeholder="Password" v-model="password" @enter="checkPassword" mandatory no-message autocomplete="current-password"/>
      <b-button variant="secondary" @click="checkPassword" :disabled="!password.length">Submit</b-button>
    </b-form>
    <p class="copyright">Design Kit is a design asset <br>service by Newtype Imageworks</p>
  </div>
</template>

<style lang="less">
@import '~@/less/proj.less';

.password-form { .flex-center(); .h(100vh); .tc; .bgc(#0D0D0D);
  > div { .w(240); .mh-c; }
  .logo { .ib; .wh(111, 24); .contain('/image/common/pwd-logo.png')}
  .logo + p { .mt(20); .fs(14); .c(#aaa); }
  [text-input] { .m(36, 0, 30); .bgc(#333); .-a(#333); .c(#fff); }
  .btn.btn-secondary { .wf; .bgc(#fff); .-a(#fff); .c(#111); .o(.9); transition: all .3s;
    &:disabled { .bgc(#E7E7E7); .-a(#E7E7E7); .c(#888); }
    .no-touch &:hover { .bgc(#fff); .-a(#fff); .c(#111); .o(1); }
  }
  .copyright { .fix; .lb(0, 40); .wf; .tc; .fs(14, 1.3); .c(#fff); .o(0.5); }
}

@media (@tl-up) {
  .password-form {
    .logo { .wh(139, 30); }
    [text-input] { .m(50, 0, 30); }
    .copyright { .lb(0, 30); }
  }
}

@media (@dm-up) {
  .password-form {
    > div { .w(300); }
  }
}
</style>