<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  mismatchMessage: { type: String, default: '' },
  emptyMessage: { type: String, default: '' },
  infoMessage: { type: String, default: '' },
  errorMessage: { type: String, default: '' },
  invalid: { type: Boolean, default: false },
  placeholder: { type: String, default: '' },
  btn: { type: String, default: '' },
  required: { type: Boolean, default: false },
  variant: { type: String, default: '' },
});

const emit = defineEmits<{
  'update:modelValue': [string | number],
  enter: [KeyboardEvent],
  esc: [KeyboardEvent],
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
    inputElement = e.target as HTMLInputElement;
  } else {
    inputElement = e as HTMLInputElement;
  }

  emit('update:modelValue', inputElement.value);
  checkValidity(inputElement);
};

const checkValidity = (input?: HTMLInputElement | HTMLTextAreaElement) => {
  if (!input) return;
  mismatch.value = !input.validity.valueMissing && input.validity.patternMismatch;
  boundInvalid.value = input.validity.rangeOverflow || input.validity.rangeUnderflow || input.validity.tooLong || input.validity.tooShort;
  generalMessage.value = input.validationMessage;
}

const invalidMessage = computed(() => {
  if (props.invalid && props.errorMessage) return props.errorMessage;
  if (mismatch.value) return props.mismatchMessage;
  if (boundInvalid.value) return generalMessage.value;
  return props.emptyMessage || generalMessage.value;
});

const onEnter = async (e: KeyboardEvent) => {
  emit('enter', e);
};

onMounted(() => {
  if (!input.value) return;
  const mutationObserver = new MutationObserver(() => checkValidity(input.value));
  mutationObserver.observe(input.value, { attributes: true });
  setTimeout(() => checkValidity(input.value), 100);
});

defineExpose({
  mountValidity: async () => onInput(input.value as HTMLInputElement | HTMLTextAreaElement).then(() => true),
  focus: () => {
    if (input.value) input.value.focus();
  },
});

</script>
<template>
  <input text-input class="form-control" ref="input" :class="[variant, { mismatch, invalid, 'bound-invalid': boundInvalid }]" :placeholder="placeholder" :required="required" :value="modelValue" :title="invalidMessage"
         v-bind="$attrs" @input="onInput" @click.stop @keyup.enter="onEnter"
         @keyup.esc="$emit('esc', $event)" @focus="$emit('focus', $event)" @blur="$emit('blur', $event)">
  <small tabindex="-1" class="form-text text-muted message info-message" v-if="infoMessage"><span>{{ infoMessage }}</span></small>
</template>
<style lang="less">
@import "~@/less/proj.less";
[text-input] { .ellipsis;
  &:not(:disabled) {
    .validated &:valid { background-image: none; .pr(12); }
    .validated &:invalid, .validated &.invalid, &.mismatch, &.bound-invalid  { .border-danger; background-image: none; .pr(12); }
  }
}
[text-input] ~ .invalid-message { .c(@c-red); .hide; }
</style>