<script setup lang="ts">
import _ from 'lodash';
import { computed, onMounted, ref } from 'vue';

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  mismatchMessage: { type: String, default: '' },
  emptyMessage: { type: String, default: '' },
  infoMessage: { type: String, default: '' },
  errorMessage: { type: String, default: '' },
  invalid: { type: Boolean, default: false },
  placeholder: { type: String, default: '' },
  required: { type: Boolean, default: false },
  readonly: { type: Boolean, default: false },
  rows: { type: Number, default: 1 },
  cols: { type: Number, default: 20 },
  resize: { type: Boolean, default: true },
  maxlength: { type: Number, default: undefined },
  maxrow: { type: Number, default: undefined },
});

const emit = defineEmits<{
  'update:modelValue': [string | number],
  focus: [FocusEvent],
  blur: [FocusEvent],
  btn: [],
  clear: [],
}>();

const mismatch = ref(false);
const boundInvalid = ref(false);
const generalMessage = ref('');
const input = ref<HTMLInputElement | HTMLTextAreaElement>();
const onInput = async (e: Event | HTMLInputElement | HTMLTextAreaElement) => {
  let inputElement: HTMLInputElement | HTMLTextAreaElement;

  if (e instanceof Event) {
    inputElement = e.target as HTMLTextAreaElement;
  } else {
    inputElement = e as HTMLTextAreaElement;
  }

  emit('update:modelValue', inputElement.value);
  checkValidity(inputElement);
  max(inputElement.value);

  if (props.maxrow) {
    const values = inputElement.value.replace(/\r\n/g, '\n').split('\n');
    if (values.length > props.maxrow) {
      inputElement.value = values.slice(0, props.maxrow).join('\n');
    }
  }
};

const max = (val: string) => {
  if (props.maxlength && val) {
    if (val.length >= props.maxlength) {
      confirm(`타이틀은 최대 ${props.maxlength}자 이내로 입력하세요.`);
    }
  }
};

const checkValidity = (input?: HTMLInputElement | HTMLTextAreaElement) => {
  if (!input) return;
  mismatch.value = !input.validity.valueMissing && input.validity.patternMismatch;
  boundInvalid.value = input.validity.rangeOverflow || input.validity.rangeUnderflow || input.validity.tooLong || input.validity.tooShort;
  generalMessage.value = input.validationMessage;
};

const invalidMessage = computed(() => {
  if (props.invalid && props.errorMessage) return props.errorMessage;
  if (mismatch.value) return props.mismatchMessage;
  if (boundInvalid.value) return generalMessage.value;
  return props.emptyMessage || generalMessage.value;
});

onMounted(() => {
  if (!input.value) return;
  const mutationObserver = new MutationObserver(() => checkValidity(input.value));
  mutationObserver.observe(input.value, { attributes: true });
  setTimeout(() => {
    checkValidity(input.value);
    resizeArea();
  }, 100);
});

const resizeArea = () => {
  const textarea = input.value;
  if (textarea) {
    textarea.style.height = 'auto';
    let height = textarea.scrollHeight;
    textarea.style.height = `${height}px`;
  }
};

defineExpose({
  mountValidity: async () => onInput(input.value as HTMLInputElement | HTMLTextAreaElement).then(() => true),
  focus: () => {
    if (input.value) input.value.focus();
  },
});
</script>

<template>
  <textarea text-box ref="input" class="form-control" :required="required" @keyup="resizeArea" @keydown="resizeArea" :value="modelValue" :maxlength="maxlength" :no-resize="!resize" :rows="rows" :cols="cols" :title="invalidMessage" @input="onInput" :placeholder="placeholder" :readonly="readonly" :class="{ mismatch, invalid, 'bound-invalid': boundInvalid }" @focus="$emit('focus', $event)" @blur="$emit('blur', $event)"></textarea>
</template>

<style lang="less">
@import '~@/less/proj.less';
[text-box] { overflow: hidden;
  &:not(:disabled) {
    .validated &:invalid, .validated &.invalid, &.mismatch, &.bound-invalid { .border-danger; }
  }
}
[text-box][no-resize] { resize: none; }
</style>
