<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import * as postApi from '@/api/post'
import * as neighborApi from '@/api/neighbor'
import * as uploadApi from '@/api/upload'
import { useThumbnail } from '@/composables/useThumbnail'
import TagChip from '@/components/TagChip.vue'
import AppButton from '@/components/AppButton.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { hasImage } = useThumbnail()

const posts = ref([])
const categories = ref([])
const tagCloud = ref([])
const blog = ref(null)
const loading = ref(false)
const page = ref(1)
const hasNext = ref(false)
const neighborType = ref(null)
const neighborLoading = ref(false)
const thumbFileInput = ref(null)
const uploadTargetPost = ref(null)
const uploadingPostId = ref(null)

const blogId = () => Number(route.params.blogId || authStore.blogId)
const isOwner = () => authStore.isLoggedIn && Number(authStore.blogId) === blogId()
const canManageNeighbor = () => authStore.isLoggedIn && !isOwner()

async function loadBlog() {
  if (!blogId()) return
  const res = await postApi.fetchBlog(blogId())
  blog.value = res.data
}

async function loadCategories() {
  if (!blogId()) return
  const res = await postApi.fetchCategories(blogId())
  categories.value = res.data
}

async function loadTagCloud() {
  if (!blogId()) return
  const res = await postApi.fetchTagCloud(blogId())
  tagCloud.value = res.data
}

async function loadPosts(reset = true) {
  if (!blogId()) return
  loading.value = true
  try {
    if (reset) {
      page.value = 1
      posts.value = []
    }
    const res = await postApi.fetchPosts({
      blogId: blogId(),
      categoryId: route.query.categoryId || undefined,
      tag: route.query.tag || undefined,
      page: page.value,
      size: 12
    })
    posts.value = reset ? res.data.items : [...posts.value, ...res.data.items]
    hasNext.value = res.data.hasNext
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value += 1
  loadPosts(false)
}

async function loadNeighborStatus() {
  neighborType.value = null
  if (!canManageNeighbor()) return
  const res = await neighborApi.fetchNeighborStatus(blogId())
  neighborType.value = res.data.type
}

async function toggleNeighbor() {
  neighborLoading.value = true
  try {
    if (neighborType.value) {
      await neighborApi.removeNeighbor(blogId())
    } else {
      await neighborApi.addNeighbor(blogId())
    }
    await loadNeighborStatus()
  } finally {
    neighborLoading.value = false
  }
}

const popularPosts = computed(() =>
  [...posts.value].sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0)).slice(0, 3)
)
const featuredPost = computed(() => posts.value[0])
const restPosts = computed(() => posts.value.slice(1))

function startThumbnailUpload(post, event) {
  event.preventDefault()
  event.stopPropagation()
  if (!isOwner()) return
  uploadTargetPost.value = post
  thumbFileInput.value?.click()
}

async function onThumbnailFileChange(e) {
  const file = e.target.files?.[0]
  e.target.value = ''
  const post = uploadTargetPost.value
  if (!file || !post) return
  uploadingPostId.value = post.id
  try {
    const uploadRes = await uploadApi.uploadImage(file)
    await postApi.updateThumbnail(post.id, uploadRes.data.url)
    post.thumbnailUrl = uploadRes.data.url
  } finally {
    uploadingPostId.value = null
    uploadTargetPost.value = null
  }
}

function photoLabel(post) {
  const prefix = post.title?.split(/[,\s]/)[0]?.trim()
  return prefix && prefix.length <= 10 ? prefix : post.categoryName || '사진'
}

function formatDate(value) {
  if (!value) return ''
  return String(value).slice(0, 10).replace(/-/g, '.')
}

function filterByCategory(categoryId) {
  router.push({ query: { ...route.query, categoryId, tag: undefined } })
}

function filterByTag(tag) {
  router.push({ query: { ...route.query, tag, categoryId: undefined } })
}

watch(() => [route.query.categoryId, route.query.tag], () => loadPosts(true))

watch(() => route.params.blogId, () => {
  loadBlog()
  loadCategories()
  loadTagCloud()
  loadNeighborStatus()
  loadPosts(true)
})

onMounted(() => {
  loadBlog()
  loadCategories()
  loadTagCloud()
  loadNeighborStatus()
  loadPosts(true)
})
</script>

<template>
  <div class="blog-home">
    <input ref="thumbFileInput" type="file" accept="image/jpeg,image/png,image/gif,image/webp" hidden @change="onThumbnailFileChange" />

    <div class="hero" v-if="blog">
      <h1>{{ blog.blogName }}</h1>
      <p>{{ blog.intro }}</p>
      <AppButton
        v-if="canManageNeighbor()"
        class="neighbor-btn"
        :variant="neighborType ? 'ghost' : 'primary'"
        :loading="neighborLoading"
        @click="toggleNeighbor"
      >
        {{ neighborType === 'MUTUAL' ? '서로이웃 · 삭제' : neighborType === 'ONE_SIDED' ? '이웃 삭제' : '이웃 추가' }}
      </AppButton>
    </div>

    <section class="popular" v-if="popularPosts.length">
      <div class="section-title">
        <span class="bar" />
        <h2>인기 글</h2>
      </div>
      <div class="popular-grid">
        <RouterLink
          v-for="(p, i) in popularPosts"
          :key="p.id"
          :to="`/posts/${p.id}`"
          class="popular-card"
        >
          <div class="popular-thumb" :style="hasImage(p.thumbnailUrl) ? { backgroundImage: `url(${p.thumbnailUrl})` } : null">
            <span class="rank">{{ i + 1 }}</span>
            <div v-if="!hasImage(p.thumbnailUrl)" class="photo-placeholder">
              <svg viewBox="0 0 24 24" class="photo-icon"><rect x="3" y="4" width="18" height="16" rx="2" fill="none" stroke="currentColor" stroke-width="1.5"/><circle cx="8.5" cy="9.5" r="1.5" fill="currentColor"/><path d="M4 17l5-5 3.5 3.5L16 12l4 4" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span class="photo-label">{{ photoLabel(p) }} 사진</span>
              <p v-if="isOwner()" class="browse-row">
                or <button type="button" class="browse-link" :disabled="uploadingPostId === p.id" @click="startThumbnailUpload(p, $event)">{{ uploadingPostId === p.id ? '업로드 중...' : 'browse files' }}</button>
              </p>
            </div>
          </div>
          <div class="popular-body">
            <p class="tag-label" v-if="p.categoryName">#{{ p.categoryName }}</p>
            <p class="popular-title">{{ p.title }}</p>
            <p class="popular-meta">조회 {{ p.viewCount }} · 좋아요 {{ p.likeCount }}</p>
          </div>
        </RouterLink>
      </div>
    </section>

    <div class="layout">
      <aside class="sidebar">
        <h3>카테고리</h3>
        <ul class="category-tree">
          <li>
            <button type="button" @click="filterByCategory(undefined)">
              <span>전체</span>
            </button>
          </li>
          <li v-for="c in categories" :key="c.id">
            <button type="button" :class="{ active: String(route.query.categoryId) === String(c.id) }" @click="filterByCategory(c.id)">
              <span>{{ c.name }}</span><span class="count">{{ c.postCount }}</span>
            </button>
          </li>
        </ul>
        <h3>태그</h3>
        <div class="tag-cloud">
          <TagChip
            v-for="t in tagCloud"
            :key="t.name"
            :label="t.name"
            clickable
            @click="filterByTag(t.name)"
          />
        </div>
      </aside>

      <main class="feed">
        <p v-if="!loading && posts.length === 0" class="empty">아직 작성된 글이 없어요 🌊</p>

        <RouterLink v-if="featuredPost" :to="`/posts/${featuredPost.id}`" class="featured">
          <div class="featured-thumb" :style="hasImage(featuredPost.thumbnailUrl) ? { backgroundImage: `url(${featuredPost.thumbnailUrl})` } : null">
            <div v-if="!hasImage(featuredPost.thumbnailUrl)" class="photo-placeholder">
              <svg viewBox="0 0 24 24" class="photo-icon"><rect x="3" y="4" width="18" height="16" rx="2" fill="none" stroke="currentColor" stroke-width="1.5"/><circle cx="8.5" cy="9.5" r="1.5" fill="currentColor"/><path d="M4 17l5-5 3.5 3.5L16 12l4 4" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span class="photo-label">{{ photoLabel(featuredPost) }} 사진</span>
              <p v-if="isOwner()" class="browse-row">
                or <button type="button" class="browse-link" :disabled="uploadingPostId === featuredPost.id" @click="startThumbnailUpload(featuredPost, $event)">{{ uploadingPostId === featuredPost.id ? '업로드 중...' : 'browse files' }}</button>
              </p>
            </div>
          </div>
          <div class="featured-body">
            <p class="featured-tag-label" v-if="featuredPost.categoryName">{{ featuredPost.categoryName }} · #{{ featuredPost.tags?.[0] || featuredPost.categoryName }}</p>
            <p class="featured-title">{{ featuredPost.title }}</p>
            <p class="featured-excerpt">{{ featuredPost.summary }}</p>
            <p class="featured-meta">조회 {{ featuredPost.viewCount }} · 댓글 {{ featuredPost.commentCount }} · 좋아요 {{ featuredPost.likeCount }}</p>
          </div>
        </RouterLink>

        <RouterLink
          v-for="p in restPosts"
          :key="p.id"
          :to="`/posts/${p.id}`"
          class="post-row"
        >
          <div class="row-thumb" :style="hasImage(p.thumbnailUrl) ? { backgroundImage: `url(${p.thumbnailUrl})` } : null">
            <div v-if="!hasImage(p.thumbnailUrl)" class="photo-placeholder mini">
              <svg viewBox="0 0 24 24" class="photo-icon"><rect x="3" y="4" width="18" height="16" rx="2" fill="none" stroke="currentColor" stroke-width="1.5"/><circle cx="8.5" cy="9.5" r="1.5" fill="currentColor"/><path d="M4 17l5-5 3.5 3.5L16 12l4 4" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span class="photo-label">{{ photoLabel(p) }}</span>
              <p v-if="isOwner()" class="browse-row">
                or <button type="button" class="browse-link" :disabled="uploadingPostId === p.id" @click="startThumbnailUpload(p, $event)">{{ uploadingPostId === p.id ? '업로드 중...' : 'browse files' }}</button>
              </p>
            </div>
          </div>
          <div class="row-body">
            <p class="row-tag-label" v-if="p.categoryName">#{{ p.categoryName }}</p>
            <p class="row-title">{{ p.title }}</p>
            <p class="row-excerpt">{{ p.summary }}</p>
            <p class="row-meta">{{ formatDate(p.publishedAt) }} · 조회 {{ p.viewCount }} · 댓글 {{ p.commentCount }} · 좋아요 {{ p.likeCount }}</p>
          </div>
        </RouterLink>

        <div class="load-more" v-if="hasNext">
          <AppButton variant="ghost" :loading="loading" @click="loadMore">더 보기</AppButton>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.blog-home {
  max-width: 1160px;
  margin: 0 auto;
}
.hero {
  text-align: center;
  padding: 56px 40px 36px;
}
.hero h1 {
  font-size: 38px;
  font-weight: 800;
  letter-spacing: -0.02em;
  margin: 0 0 12px;
}
.hero p {
  font-size: 16px;
  color: var(--color-ink-soft);
  margin: 0;
}
.neighbor-btn {
  margin-top: 20px;
}

.popular {
  padding: 0 40px 48px;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.section-title .bar {
  width: 4px;
  height: 20px;
  background: var(--color-primary);
  border-radius: 2px;
}
.section-title h2 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
}
.popular-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}
@media (min-width: 768px) {
  .popular-grid { grid-template-columns: repeat(3, 1fr); }
}
.popular-card {
  display: block;
  min-width: 0;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-card);
  text-decoration: none;
  color: inherit;
}
.popular-thumb {
  position: relative;
  height: 140px;
  background-size: cover;
  background-position: center;
}
.rank {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1;
  width: 26px;
  height: 26px;
  border-radius: 8px;
  background: var(--color-primary);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}
.popular-body {
  padding: 16px;
}
.tag-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  margin: 0 0 6px;
}
.featured-tag-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-primary);
  margin: 0 0 10px;
}
.row-tag-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  margin: 0 0 4px;
}
.popular-title {
  font-size: 15px;
  font-weight: 700;
  line-height: 1.4;
  margin: 0 0 8px;
}
.popular-meta, .row-meta {
  font-size: 12px;
  color: var(--color-ink-soft);
  margin: 0;
}
.featured-meta {
  font-size: 13px;
  color: var(--color-ink-soft);
  margin: 0;
}

.photo-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: var(--color-placeholder-bg);
  border-radius: inherit;
}
.photo-icon {
  width: 32px;
  height: 32px;
  color: var(--color-placeholder-text);
}
.photo-placeholder.mini .photo-icon {
  width: 22px;
  height: 22px;
}
.photo-label {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-placeholder-text);
}
.browse-row {
  margin: 0;
  font-size: 12px;
  color: var(--color-placeholder-text);
}
.browse-link {
  background: none;
  border: none;
  padding: 0;
  font: inherit;
  font-size: 12px;
  color: var(--color-primary);
  text-decoration: underline;
  cursor: pointer;
}
.browse-link:disabled {
  cursor: wait;
  opacity: 0.7;
}
.photo-placeholder.mini {
  gap: 3px;
}
.photo-placeholder.mini .photo-label {
  font-size: 11px;
}
.photo-placeholder.mini .browse-row {
  font-size: 11px;
}
.photo-placeholder.mini .browse-link {
  font-size: 11px;
}

.layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 40px;
  padding: 0 40px 80px;
}
@media (min-width: 768px) {
  .layout {
    grid-template-columns: 220px 1fr;
  }
}
.sidebar, .feed {
  min-width: 0;
}
.sidebar h3 {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-ink-soft);
  text-transform: uppercase;
  letter-spacing: .06em;
  margin: 0 0 12px;
}
.sidebar h3:not(:first-child) {
  margin-top: 32px;
}
.category-tree {
  list-style: none;
  padding: 0;
  margin: 0;
}
.category-tree button {
  display: flex;
  justify-content: space-between;
  width: 100%;
  background: none;
  border: none;
  padding: 8px 10px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  color: var(--color-ink);
}
.category-tree button .count {
  color: var(--color-ink-soft);
}
.category-tree button.active {
  color: var(--color-primary);
  font-weight: 700;
}
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.featured {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 32px;
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  padding-bottom: 32px;
  margin-bottom: 32px;
  border-bottom: 1px solid var(--color-border);
}
.featured-thumb {
  position: relative;
  min-width: 0;
  height: 280px;
  border-radius: 20px;
  background-size: cover;
  background-position: center;
}
.featured-body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}
.featured-title {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: -0.01em;
  line-height: 1.35;
  margin: 0 0 14px;
}
.featured-excerpt {
  font-size: 14px;
  color: var(--color-ink-soft);
  line-height: 1.6;
  margin: 0 0 16px;
}

.post-row {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 20px;
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  padding: 20px 0;
  border-bottom: 1px solid var(--color-border);
}
.row-thumb {
  position: relative;
  width: 120px;
  height: 90px;
  border-radius: 12px;
  background-size: cover;
  background-position: center;
  flex-shrink: 0;
}
.row-body {
  min-width: 0;
}
.row-title {
  font-size: 17px;
  font-weight: 700;
  margin: 0 0 6px;
  line-height: 1.4;
}
.row-excerpt {
  font-size: 13px;
  color: var(--color-ink-soft);
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty {
  text-align: center;
  color: var(--color-ink-soft);
  padding: 48px 0;
}
.load-more {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
