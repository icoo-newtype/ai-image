<script setup lang="ts">
import { ref } from 'vue';
import { useState } from '@/store/state.js';
import Modal from '@/views/components/common/Modal.vue';
import BFormGroup from '@/views/components/common/BFormGroup.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import BInputGroup from '@/views/components/common/BInputGroup.vue';
import BButton from '@/views/components/common/BButton.vue';
import BForm from '@/views/components/common/BForm.vue';
import DropSelect from '@/views/components/form/DropSelect.vue';

const emit = defineEmits<{ resolve: [string], reject: [] }>();

const state = useState();
const open = ref(true);
const password = ref('');
const passwordRepeat = ref('');
const validator = ref();

const reject = () => open.value = false;
const resolve = () => {
  if (!validator.value?.validate()) return;
  emit('resolve', password.value);
  open.value = false;
};
</script>

<template>
  <Modal v-model="open" size="md" title="비밀번호 등록">
    <b-form ref="validator">
      <b-form-group label="새 비밀번호" label-for="newPwd">
        <text-input id="newPwd" v-model="password" pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[\]\[\{\}\|\(\)\/\-`~!@#$%^&*_+=,.<>?:;'&quot;]).{8,16}$" autocomplete="new-password" required type="password" mismatch-message="영문, 숫자, 특수문자 조합 8~16자로 입력해주세요" info-message="영문, 숫자, 특수문자 조합 8~16자로 입력해주세요"/>
      </b-form-group>
      <b-form-group class="mb-0" label="새 비밀번호 확인" label-for="repeat">
        <text-input id="repeat" v-model="passwordRepeat" autocomplete="new-password" required type="password" :pattern="password" mismatch-message="비밀번호가 일치하지 않습니다."/>
      </b-form-group>
    </b-form>
    <div class="mt-80 tr">
      <b-button class="mr-2" @click="reject">취소</b-button>
      <b-button @click="resolve" variant="primary">확인</b-button>
    </div>
  </Modal>
</template>
