<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import * as postApi from '@/api/post'
import * as uploadApi from '@/api/upload'
import AppButton from '@/components/AppButton.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const postId = computed(() => (route.params.id ? Number(route.params.id) : null))
const isEdit = computed(() => postId.value !== null)

const title = ref('')
const content = ref('')
const categoryId = ref('')
const visibility = ref('PUBLIC')
const status = ref('PUBLISHED')
const tagsText = ref('')
const categories = ref([])
const saving = ref(false)
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const autoSavedAt = ref(null)
const thumbnailUrl = ref('')
const thumbnailUploading = ref(false)
const thumbnailError = ref('')
const thumbnailInput = ref(null)
const contentInput = ref(null)
const contentImageInput = ref(null)
const contentImageUploading = ref(false)

let autoSaveTimer = null

function selectContentImage() {
  contentImageInput.value?.click()
}

async function onContentImageChange(e) {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  contentImageUploading.value = true
  try {
    const res = await uploadApi.uploadImage(file)
    const textarea = contentInput.value
    const start = textarea?.selectionStart ?? content.value.length
    const end = textarea?.selectionEnd ?? content.value.length
    const needsLeadingNewline = start > 0 && content.value[start - 1] !== '\n'
    const insertion = `${needsLeadingNewline ? '\n' : ''}![](${res.data.url})\n`
    content.value = content.value.slice(0, start) + insertion + content.value.slice(end)
    const cursorPos = start + insertion.length
    if (textarea) {
      requestAnimationFrame(() => {
        textarea.focus()
        textarea.setSelectionRange(cursorPos, cursorPos)
      })
    }
  } catch (err) {
    errorMessage.value = err?.response?.data?.message || '이미지 업로드에 실패했습니다.'
  } finally {
    contentImageUploading.value = false
  }
}

function selectThumbnail() {
  thumbnailInput.value?.click()
}

async function onThumbnailChange(e) {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  thumbnailError.value = ''
  thumbnailUploading.value = true
  try {
    const res = await uploadApi.uploadImage(file)
    thumbnailUrl.value = res.data.url
  } catch (err) {
    thumbnailError.value = err?.response?.data?.message || '이미지 업로드에 실패했습니다.'
  } finally {
    thumbnailUploading.value = false
  }
}

function removeThumbnail() {
  thumbnailUrl.value = ''
}

async function loadCategories() {
  if (!authStore.blogId) return
  const res = await postApi.fetchCategories(authStore.blogId)
  categories.value = res.data
}

async function loadForEdit() {
  loading.value = true
  try {
    const res = await postApi.fetchPost(postId.value)
    const post = res.data
    if (post.authorUserId !== authStore.userId) {
      loadFailed.value = true
      errorMessage.value = '본인의 게시글만 수정할 수 있습니다.'
      return
    }
    title.value = post.title
    content.value = post.content
    categoryId.value = post.categoryId || ''
    visibility.value = post.visibility
    status.value = post.status
    tagsText.value = (post.tags || []).join(', ')
    thumbnailUrl.value = post.thumbnailUrl || ''
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e?.response?.data?.message || '게시글을 불러올 수 없습니다.'
  } finally {
    loading.value = false
  }
}

function buildPayload(nextStatus) {
  return {
    title: title.value,
    content: content.value,
    categoryId: categoryId.value || null,
    thumbnailUrl: thumbnailUrl.value || null,
    visibility: visibility.value,
    status: nextStatus,
    tags: tagsText.value.split(',').map((t) => t.trim()).filter(Boolean)
  }
}

function autoSaveDraft() {
  if (isEdit.value) return
  localStorage.setItem('post-draft', JSON.stringify(buildPayload('DRAFT')))
  autoSavedAt.value = new Date()
}

async function submit(nextStatus) {
  errorMessage.value = ''
  if (!title.value.trim() || !content.value.trim()) {
    errorMessage.value = '제목과 본문을 입력해주세요.'
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await postApi.updatePost(postId.value, buildPayload(nextStatus))
      router.push(`/posts/${postId.value}`)
    } else {
      const res = await postApi.createPost(buildPayload(nextStatus))
      localStorage.removeItem('post-draft')
      router.push(`/posts/${res.data.id}`)
    }
  } catch (e) {
    errorMessage.value = e?.response?.data?.message || '게시글 저장에 실패했습니다.'
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  await loadCategories()

  if (isEdit.value) {
    await loadForEdit()
    return
  }

  const draft = localStorage.getItem('post-draft')
  if (draft) {
    const parsed = JSON.parse(draft)
    title.value = parsed.title || ''
    content.value = parsed.content || ''
    categoryId.value = parsed.categoryId || ''
    visibility.value = parsed.visibility || 'PUBLIC'
    thumbnailUrl.value = parsed.thumbnailUrl || ''
    tagsText.value = (parsed.tags || []).join(', ')
  }
  autoSaveTimer = setInterval(autoSaveDraft, 30000)
})

onUnmounted(() => {
  if (autoSaveTimer) clearInterval(autoSaveTimer)
})
</script>

<template>
  <div class="editor" v-if="loadFailed">
    <p class="error">{{ errorMessage }}</p>
  </div>
  <div class="editor" v-else>
    <div class="topbar">
      <select v-model="categoryId">
        <option value="">카테고리 없음</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <select v-model="visibility">
        <option value="PUBLIC">전체공개</option>
        <option value="NEIGHBOR">이웃공개</option>
        <option value="PRIVATE">비공개</option>
      </select>
      <span v-if="autoSavedAt" class="autosave">{{ autoSavedAt.toLocaleTimeString() }} 자동저장됨</span>
      <div class="spacer" />
      <AppButton variant="ghost" size="sm" @click="submit('DRAFT')">임시저장</AppButton>
      <AppButton size="sm" :loading="saving" @click="submit('PUBLISHED')">{{ isEdit ? '수정 완료' : '발행' }}</AppButton>
    </div>

    <div class="thumbnail-field">
      <input ref="thumbnailInput" type="file" accept="image/jpeg,image/png,image/gif,image/webp" hidden @change="onThumbnailChange" />
      <div v-if="thumbnailUrl" class="thumbnail-preview" :style="{ backgroundImage: `url(${thumbnailUrl})` }">
        <button type="button" class="thumbnail-remove" @click="removeThumbnail">제거</button>
      </div>
      <button v-else type="button" class="thumbnail-placeholder" :disabled="thumbnailUploading" @click="selectThumbnail">
        {{ thumbnailUploading ? '업로드 중...' : '+ 썸네일 이미지 추가' }}
      </button>
      <p v-if="thumbnailError" class="error">{{ thumbnailError }}</p>
    </div>

    <input v-model="title" class="title-input" placeholder="제목을 입력하세요" maxlength="200" />

    <div class="content-toolbar">
      <input ref="contentImageInput" type="file" accept="image/jpeg,image/png,image/gif,image/webp" hidden @change="onContentImageChange" />
      <button type="button" class="insert-image-btn" :disabled="contentImageUploading" @click="selectContentImage">
        {{ contentImageUploading ? '업로드 중...' : '🖼️ 이미지 삽입' }}
      </button>
      <span class="toolbar-hint">본문에서 사진을 넣고 싶은 위치에 커서를 두고 클릭하세요</span>
    </div>
    <textarea
      ref="contentInput"
      v-model="content"
      class="content-input"
      placeholder="이야기를 적어보세요... (마크다운 문법 지원: # 제목, **굵게**, - 목록, > 인용 등)"
      rows="16"
    />
    <input v-model="tagsText" class="tag-input" placeholder="태그를 콤마로 구분해 입력하세요 (예: 일상, 여행)" />

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<style scoped>
.editor {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 16px 64px;
}
.topbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.topbar select {
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 6px 10px;
}
.autosave {
  font-size: 12px;
  color: var(--color-ink-soft);
}
.spacer { flex: 1; }
.thumbnail-field {
  margin-bottom: 16px;
}
.thumbnail-placeholder {
  width: 100%;
  aspect-ratio: 16 / 9;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius);
  background: var(--color-surface);
  color: var(--color-ink-soft);
  font-size: 15px;
  cursor: pointer;
}
.thumbnail-placeholder:disabled {
  cursor: wait;
  opacity: 0.7;
}
.thumbnail-preview {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: var(--radius);
  background-size: cover;
  background-position: center;
  border: 1px solid var(--color-border);
}
.thumbnail-remove {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  border: none;
  border-radius: 9999px;
  padding: 6px 14px;
  font-size: 13px;
  cursor: pointer;
}
.title-input {
  width: 100%;
  font-family: var(--font-heading);
  font-size: 28px;
  border: none;
  border-bottom: 2px solid var(--color-border);
  padding: 8px 0;
  margin-bottom: 16px;
  background: transparent;
}
.title-input:focus { outline: none; }
.content-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.insert-image-btn {
  border: 1px solid var(--color-border);
  border-radius: 9999px;
  padding: 6px 14px;
  font-size: 13px;
  background: var(--color-surface);
  color: var(--color-ink);
  cursor: pointer;
}
.insert-image-btn:disabled {
  cursor: wait;
  opacity: 0.7;
}
.toolbar-hint {
  font-size: 12px;
  color: var(--color-ink-soft);
}
.content-input {
  width: 100%;
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  padding: 16px;
  font-family: var(--font-body);
  font-size: 15px;
  resize: vertical;
  background: var(--color-surface);
  margin-bottom: 16px;
}
.tag-input {
  width: 100%;
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 10px 12px;
}
.error {
  color: var(--color-error);
  margin-top: 12px;
}
</style>
