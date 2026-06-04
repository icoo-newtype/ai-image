<script setup lang="ts">
import { EditorContent, useEditor } from '@tiptap/vue-3';
import { computed, ref } from 'vue';
import { StarterKit } from '@tiptap/starter-kit';
import TextStyle from '@tiptap/extension-text-style';
import Underline from '@tiptap/extension-underline';
import Superscript from '@tiptap/extension-superscript';
import Subscript from '@tiptap/extension-subscript';
import TextAlign from '@tiptap/extension-text-align';
import { Color } from '@tiptap/extension-color';
import Link from '@tiptap/extension-link';
import Placeholder from '@tiptap/extension-placeholder';
import { useInputDialog } from '@/hooks/useInputDialog';
import isEmpty from 'lodash-es/isEmpty';
import oax from '@/utils/oax';
import BForm from '@/views/components/common/BForm.vue';
import TextInput from '@/views/components/form/TextInput.vue';
import Modal from '@/views/components/common/Modal.vue';
import BFormGroup from '@/views/components/common/BFormGroup.vue';
import BButton from '@/views/components/common/BButton.vue';

const props = defineProps({
  modelValue: { type: String },
  type: { type: String, default: null },
  desc: { type: Boolean, default: false },
  placeholder: { type: String, default: '텍스트를 입력해 주세요.' },
  all: { type: Boolean, default: true },
  bold: { type: Boolean, default: false },
  color: { type: Boolean, default: false },
  link: { type: Boolean, default: false },
  remark: { type: Boolean, default: false },
  align: { type: Boolean, default: false },
  idx: { type: Number },
  required: { type: Boolean, default: false },
});
const emit = defineEmits(['update:modelValue']);

type correction = {
  original: string;
  reason: string;
  replacement: string;
}

const el = ref<HTMLElement>();
const controller = ref<HTMLElement>();
const control = ref(false);
const isOver = ref(false);
const corrections = ref<correction[]>([]);
const loading = ref(false);
const aiModal = ref(false);

const invalid = computed(() => props.required && !editor.value?.getText());

const inputDialog = useInputDialog();

const editor = useEditor({
  parseOptions: {
    preserveWhitespace: 'full',
  },
  content: props.modelValue,
  extensions: [StarterKit, TextStyle, Color, Underline, Superscript, Subscript,
    Placeholder.configure({ placeholder: props.placeholder }),
    TextAlign.configure({
      types: ['heading', 'paragraph'],
    }),
    Link.configure({
      openOnClick: false,
    }),
  ],
  onUpdate({ editor }) {
    let text = editor.getHTML();
    text = text.replace(/(\s{2,})/g, (match: any, space: any) => space.replace(/\s/g, '&nbsp;'));
    emit('update:modelValue', text);
  },
  onFocus() {
    control.value = true;
  },
  onBlur() {
    if (!isOver.value) control.value = false;
  }
});

const openLink = async () => {
  const link = await inputDialog('Link', 'url을 입력해주세요.', 'https://example.com');
  if (isEmpty(link)) {
    editor?.value?.chain().focus().extendMarkRange('link').unsetLink().run();
    return;
  }
  // update link
  editor?.value?.chain().focus().extendMarkRange('link').setLink({ href: link }).run();
};

function replaceTextInEditor(original: string, replacement: string) {
  const { state, view } = editor.value!;
  const { tr, doc } = state;

  doc.descendants((node, pos) => {
    if (!node.isText || !node.text) return;

    const index = node.text.indexOf(original);
    if (index === -1) return;

    tr.replaceWith(
        pos + index,
        pos + index + original.length,
        state.schema.text(replacement, node.marks) // 기존 marks(서식) 유지
    );
  });

  if (tr.docChanged) {
    view.dispatch(tr);
    emit('update:modelValue', editor.value?.getHTML() ?? '');
  }
}

function applyCorrection(item: correction, index: number) {
  replaceTextInEditor(item.original, item.replacement);
  corrections.value.splice(index, 1);
  if (corrections.value.length === 0) {
    aiModal.value = false;
  }
}

function applyCorrectionAll() {
  corrections.value.forEach(item => {
    replaceTextInEditor(item.original, item.replacement);
  });
  corrections.value = [];
  aiModal.value = false;
}

const checkGrammar = async () => {
  const text: string = editor.value?.getText() ?? '';
  if (!text.trim()) {
    alert('내용을 입력해주세요.');
    return;
  }

  loading.value = true;
  corrections.value = [];
  aiModal.value = true;

  try {
    const response = await oax.post<any>(`/api/admin/grammarCheck`, { text });

    const aiResponse = response.data.candidates[0].content.parts[0].text;
    const jsonString = aiResponse.replace(/```json|```/g, '').trim();
    const parsedData = JSON.parse(jsonString);

    corrections.value = parsedData.corrections;
  } catch (e) {
    aiModal.value = false;
    alert('교정 중 오류가 발생했습니다.');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div editor :class="{ invalid }">
    <div :class="type" ref="el">
      <div v-if="editor" class="controller" :class="{ hidden: !control }" @mouseover="isOver=true" @mouseleave="isOver=false" ref="controller">
        <nav>
          <a class="btn" @click="checkGrammar">
            <img alt="" src="/admin_image/module/text-control-ai.svg">
          </a>
          <a class="btn" v-if="bold || all" :class="{ 'is-active': editor.isActive('bold') }" @click="editor.chain().focus().toggleBold().run();">
            <img alt="" src="/admin_image/module/text-control-bold.png">
          </a>
          <a class="btn" v-if="link || all" :class="{ 'is-active': editor.isActive('link') }" @click="openLink">
            <img alt="" src="/admin_image/module/text-control-link.png">
          </a>
          <div v-if="color || all" class="color-pick">
            <input type="color" @input="editor.chain().focus().setColor(($event.target as HTMLInputElement).value).run()" :value="editor.getAttributes('textStyle')?.color || '#111111'">
          </div>
          <a class="btn" v-if="all" :class="{ 'is-active': editor.isActive('italic') }" @click="editor.chain().focus().toggleItalic().run()">
            <img alt="" src="/admin_image/module/text-control-italic.png">
          </a>
          <a class="btn" v-if="align || all" :class="{ 'is-active': editor.isActive({textAlign: 'center'}) }" @click="editor.chain().focus().setTextAlign('center').run()">
            <img alt="" src="/admin_image/module/text-control-center.png">
          </a>
          <a class="btn" v-if="align || all" :class="{ 'is-active': editor.isActive({textAlign: 'left'}) }" @click="editor.chain().focus().setTextAlign('left').run()">
            <img alt="" src="/admin_image/module/text-control-left.png">
          </a>
          <a class="btn" v-if="align || all" :class="{ 'is-active': editor.isActive({textAlign: 'right'}) }" @click="editor.chain().focus().setTextAlign('right').run()">
            <img alt="" src="/admin_image/module/text-control-right.png">
          </a>
          <a class="btn" v-if="remark || all" :class="{ 'is-active': editor.isActive('superscript') }" @click="editor.chain().focus().toggleSuperscript().run()">
            <img alt="" src="/admin_image/module/text-control-superscript.png">
          </a>
          <a class="btn" v-if="remark || all" :class="{ 'is-active': editor.isActive('subscript') }" @click="editor.chain().focus().toggleSubscript().run()">
            <img alt="" src="/admin_image/module/text-control-subscript.png">
          </a>
        </nav>
      </div>
      <editor-content v-if="editor" :editor="editor" class="editor-content" :key="`editor-${idx}`"/>
    </div>

    <Modal id="grammar-modal" title="AI 교정" v-model="aiModal" size="lg">
      <div class="loading" v-if="loading">
        <div class="loader-spinner"></div>
      </div>
      <template v-else>
        <div v-for="(row, i) in corrections" :key="i" class="item">
          <div class="correction">
            <p>{{ row.original }}</p>
            <p class="replace">{{ row.replacement }}</p>
            <b-button size="sm" @click="applyCorrection(row, i)" variant="primary">변경</b-button>
          </div>
          <p class="reason">{{ row.reason }}</p>
        </div>
      </template>
      <div class="mt-40 tc" v-if="!loading">
        <b-button @click="applyCorrectionAll" variant="primary">모두 변경</b-button>
      </div>
    </Modal>
  </div>
</template>

<style lang="less">
@import '~@/less/proj';

.validated .invalid .editor-content { .-a(@c-red, 1); }

#grammar-modal {
  .correction { .rel; .p(16, 16, 50); .br(4); .-a(#ddd);
    .replace { .mt(12); .c(@c-blue); .bold; }
    .btn { .abs; .rb(16, 16); }
  }
  .reason { .mt(8); .c(#898989); .fs(12, 1.3); }
  .item + .item { .mt(40); }

  .loading { .rel; .h(400); }
  .loader-spinner {
    .abs; .lt(50%, 50%); .t-xyc; .mt(-30);
    width: 24px;
    height: 24px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #64bcea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
}
</style>