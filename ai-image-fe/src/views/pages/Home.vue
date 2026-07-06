<template>
  <div class="ai-image-page">
    <div class="inner">
      <h1 class="title">AI Image Generator</h1>

      <!-- 모델 선택 -->
      <div class="model-select-wrap">
        <button
          v-for="m in models"
          :key="m.value"
          class="model-btn"
          :class="{ active: selectedModel === m.value }"
          :disabled="loading"
          @click="selectedModel = m.value"
        >
          {{ m.label }}
        </button>
      </div>

      <!-- 프롬프트 입력 영역 -->
      <div class="prompt-section">
        <textarea
          ref="textareaRef"
          v-model="prompt"
          class="prompt-input"
          placeholder="Describe the image you want to create..."
          :disabled="loading"
          @keydown.ctrl.enter="generate"
          @input="autoResize"
        />
        <button
          class="create-btn"
          :disabled="!prompt.trim() || loading"
          @click="generate"
        >
          <span v-if="loading" class="loading-spinner" />
          <span>{{ loading ? 'Creating...' : 'Create' }}</span>
        </button>
      </div>

      <!-- 에러 메시지 -->
      <p v-if="error" class="error-msg">{{ error }}</p>

      <!-- 이미지 결과 영역 -->
      <div v-if="imageUrl" class="result-section">
        <div class="result-header">
          <span class="result-label">Generated Image</span>
          <a :href="imageUrl" download="ai-image.png" class="download-btn">Download</a>
        </div>
        <div class="image-wrap">
          <img :src="imageUrl" alt="Generated image" />
        </div>
        <p class="prompt-echo">{{ usedPrompt }}</p>
      </div>

      <!-- 빈 상태 -->
      <div v-else-if="!loading" class="empty-section">
        <div class="empty-icon">✦</div>
        <p>Enter a prompt and press Create to generate an image</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const models = [
  { label: 'GPT Image', value: 'gpt-image-1' },
  { label: 'Gemini', value: 'gemini-imagen' },
];

const selectedModel = ref('gpt-image-1');
const textareaRef = ref<HTMLTextAreaElement>();

function autoResize() {
  const el = textareaRef.value;
  if (!el) return;
  el.style.height = 'auto';
  el.style.height = `${el.scrollHeight}px`;
}
const prompt = ref('');
const usedPrompt = ref('');
const imageUrl = ref('');
const loading = ref(false);
const error = ref('');

async function generateWithGPT() {
  const apiKey = import.meta.env.VITE_OPENAI_API_KEY;
  const response = await fetch('https://api.openai.com/v1/images/generations', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`,
    },
    body: JSON.stringify({
      model: 'gpt-image-1',
      prompt: prompt.value,
      n: 1,
      size: '1024x1024',
    }),
  });
  if (!response.ok) {
    const err = await response.json();
    throw new Error(err.error?.message || 'Image generation failed');
  }
  const data = await response.json();
  const b64 = data.data[0].b64_json;
  return b64 ? `data:image/png;base64,${b64}` : data.data[0].url;
}

async function generateWithGemini() {
  const apiKey = import.meta.env.VITE_GEMINI_API_KEY;
  const response = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/imagen-4.0-generate-001:predict?key=${apiKey}`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        instances: [{ prompt: prompt.value }],
        parameters: { sampleCount: 1 },
      }),
    }
  );
  if (!response.ok) {
    const err = await response.json();
    throw new Error(err.error?.message || 'Image generation failed');
  }
  const data = await response.json();
  const b64 = data.predictions?.[0]?.bytesBase64Encoded;
  if (!b64) throw new Error('No image returned from Gemini');
  return `data:image/png;base64,${b64}`;
}

async function generate() {
  if (!prompt.value.trim() || loading.value) return;

  loading.value = true;
  error.value = '';
  imageUrl.value = '';
  usedPrompt.value = prompt.value;

  try {
    imageUrl.value = selectedModel.value === 'gemini-imagen'
      ? await generateWithGemini()
      : await generateWithGPT();
  } catch (e: any) {
    error.value = e.message || 'An error occurred. Please try again.';
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped lang="less">
.ai-image-page {
  min-height: 100vh;
  background: #0a0a0a;
  color: #fff;
  display: flex;
  justify-content: center;
  padding: 80px 20px;
}

.inner {
  width: 100%;
  max-width: 800px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 48px;
  letter-spacing: -0.5px;
  color: #fff;
}

/* 모델 선택 */
.model-select-wrap {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.model-btn {
  padding: 8px 20px;
  border-radius: 999px;
  border: 1px solid #2e2e2e;
  background: transparent;
  color: #666;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;

  &:hover:not(:disabled) { border-color: #555; color: #aaa; }
  &:disabled { cursor: not-allowed; opacity: 0.4; }
  &.active { background: #fff; color: #0a0a0a; border-color: #fff; }
}

/* 프롬프트 영역 */
.prompt-section {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-bottom: 16px;
}

.prompt-input {
  flex: 1;
  min-height: 100px;
  height: 100px;
  background: #1a1a1a;
  border: 1px solid #2e2e2e;
  border-radius: 12px;
  color: #fff;
  font-size: 15px;
  line-height: 1.6;
  padding: 16px;
  resize: none;
  transition: border-color 0.2s;
  font-family: inherit;

  &::placeholder { color: #555; }
  &:focus { outline: none; border-color: #555; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.create-btn {
  align-self: stretch;
  height: auto;
  min-height: 100px;
  padding: 0 32px;
  background: #fff;
  color: #0a0a0a;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  transition: background 0.2s, opacity 0.2s;
  font-family: inherit;

  &:hover:not(:disabled) { background: #e0e0e0; }
  &:disabled { opacity: 0.4; cursor: not-allowed; }
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #0a0a0a;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 에러 */
.error-msg {
  color: #ff5c5c;
  font-size: 14px;
  margin-bottom: 24px;
}

/* 결과 영역 */
.result-section {
  margin-top: 48px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.result-label {
  font-size: 13px;
  color: #666;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.download-btn {
  font-size: 13px;
  color: #fff;
  border: 1px solid #2e2e2e;
  border-radius: 6px;
  padding: 6px 14px;
  text-decoration: none;
  transition: border-color 0.2s;

  &:hover { border-color: #666; }
}

.image-wrap {
  border-radius: 16px;
  overflow: hidden;
  background: #111;

  img {
    width: 100%;
    display: block;
  }
}

.prompt-echo {
  margin-top: 16px;
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}

/* 빈 상태 */
.empty-section {
  margin-top: 80px;
  text-align: center;
  color: #333;

  .empty-icon {
    font-size: 40px;
    margin-bottom: 16px;
  }

  p { font-size: 15px; }
}
</style>
