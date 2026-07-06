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

      <!-- 이미지 갤러리 -->
      <div v-if="imageList.length > 0" class="gallery-section">
        <h2 class="gallery-title">Gallery</h2>
        <div class="gallery-grid">
          <div v-for="item in imageList" :key="item.sq" class="gallery-item" @click="openModal(item)">
            <img :src="item.url" :alt="item.prompt" />
            <div class="gallery-overlay">
              <p class="gallery-prompt">{{ item.prompt }}</p>
              <span class="gallery-model">{{ item.model }}</span>
            </div>
          </div>
        </div>
        <div ref="sentinelRef" class="sentinel">
          <span v-if="listLoading" class="list-spinner" />
        </div>
      </div>
    </div>

    <!-- 이미지 상세 팝업 -->
    <Teleport to="body">
      <div v-if="modalItem" class="modal-backdrop" @click.self="closeModal">
        <div class="modal">
          <button class="modal-close" @click="closeModal">✕</button>
          <div class="modal-image-wrap">
            <img :src="modalItem.url" :alt="modalItem.prompt" />
          </div>
          <div class="modal-info">
            <p class="modal-prompt">{{ modalItem.prompt }}</p>
            <div class="modal-meta">
              <span class="modal-model">{{ modalItem.model }}</span>
              <span class="modal-date">{{ formatDate(modalItem.regDtt) }}</span>
            </div>
            <a :href="modalItem.url" :download="`ai-image-${modalItem.sq}.png`" target="_blank" class="modal-download-btn">
              Download
            </a>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { GoogleGenAI } from '@google/genai';
import oax from '@/utils/oax';

interface ImageItem {
  sq: number;
  prompt: string;
  model: string;
  url: string;
  regDtt: string;
  actor: string;
}

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
const imageList = ref<ImageItem[]>([]);
const lastSq = ref(0);
const hasMore = ref(true);
const listLoading = ref(false);
const sentinelRef = ref<HTMLElement>();
const modalItem = ref<ImageItem | null>(null);

function openModal(item: ImageItem) { modalItem.value = item; }
function closeModal() { modalItem.value = null; }
function formatDate(dtt: string) {
  if (!dtt) return '';
  return dtt.replace('T', ' ').slice(0, 16);
}

async function generateWithGPT(): Promise<string> {
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
  return b64 ? b64 : null;
}

async function generateWithGemini(): Promise<string> {
  const apiKey = import.meta.env.VITE_GEMINI_API_KEY;
  const ai = new GoogleGenAI({ apiKey });
  const response = await ai.models.generateContent({
    model: 'gemini-3.1-flash-image',
    contents: [{ role: 'user', parts: [{ text: prompt.value }] }],
    config: { responseModalities: ['IMAGE', 'TEXT'] },
  });
  for (const part of response.candidates?.[0]?.content?.parts ?? []) {
    if (part.inlineData?.data) {
      return part.inlineData.data;
    }
  }
  throw new Error('No image returned from Gemini');
}

async function saveImage(b64Image: string) {
  try {
    await oax.postJson('/api/admin/image/save', {
      b64Image,
      prompt: usedPrompt.value,
      model: selectedModel.value,
    });
    // 저장 후 리스트 초기화해서 맨 위부터 다시 로드
    imageList.value = [];
    lastSq.value = 0;
    hasMore.value = true;
    await loadMore();
  } catch (e) {
    // silent fail
  }
}

async function loadMore() {
  if (listLoading.value || !hasMore.value) return;
  listLoading.value = true;
  try {
    const { data } = await oax.get<ImageItem[]>('/api/image/list', {
      lastSq: lastSq.value,
      size: 15,
    });
    if (data.length < 15) hasMore.value = false;
    if (data.length > 0) {
      imageList.value.push(...data);
      lastSq.value = data[data.length - 1].sq;
    }
  } catch (e) {
    // silent fail
  } finally {
    listLoading.value = false;
  }
}

async function generate() {
  if (!prompt.value.trim() || loading.value) return;

  loading.value = true;
  error.value = '';
  imageUrl.value = '';
  usedPrompt.value = prompt.value;

  try {
    const b64 = selectedModel.value === 'gemini-imagen'
      ? await generateWithGemini()
      : await generateWithGPT();

    imageUrl.value = `data:image/png;base64,${b64}`;
    await saveImage(b64);
  } catch (e: any) {
    error.value = e.message || 'An error occurred. Please try again.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadMore();

  const observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) loadMore();
  }, { threshold: 0.1 });

  const checkSentinel = () => {
    if (sentinelRef.value) {
      observer.observe(sentinelRef.value);
    } else {
      setTimeout(checkSentinel, 100);
    }
  };
  checkSentinel();
});
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

/* 갤러리 */
.gallery-section {
  margin-top: 80px;
}

.gallery-title {
  font-size: 13px;
  color: #666;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 24px;
  font-weight: 500;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.gallery-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: #111;
  aspect-ratio: 1;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.3s;
  }

  &:hover img { transform: scale(1.03); }
  &:hover .gallery-overlay { opacity: 1; }
}

.gallery-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.85) 0%, transparent 50%);
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 16px;
}

.gallery-prompt {
  font-size: 12px;
  color: #ddd;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 6px;
}

.gallery-model {
  font-size: 11px;
  color: #888;
}

.sentinel {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 8px;
}

.list-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #333;
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}


</style>

<style lang="less">
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 24px;
}

.modal {
  background: #111;
  border-radius: 16px;
  overflow: hidden;
  max-width: 640px;
  width: 100%;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.6);
  border: none;
  border-radius: 50%;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  transition: background 0.2s;

  &:hover { background: rgba(255,255,255,0.15); }
}

.modal-image-wrap {
  width: 100%;
  aspect-ratio: 1;
  background: #0a0a0a;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    display: block;
  }
}

.modal-info {
  padding: 20px;
}

.modal-prompt {
  font-size: 14px;
  color: #ccc;
  line-height: 1.6;
  margin-bottom: 12px;
}

.modal-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.modal-model {
  font-size: 12px;
  color: #666;
  background: #1e1e1e;
  padding: 3px 10px;
  border-radius: 999px;
}

.modal-date {
  font-size: 12px;
  color: #555;
  display: flex;
  align-items: center;
}

.modal-download-btn {
  display: inline-block;
  padding: 10px 24px;
  background: #fff;
  color: #0a0a0a;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
  transition: background 0.2s;

  &:hover { background: #e0e0e0; }
}
</style>
