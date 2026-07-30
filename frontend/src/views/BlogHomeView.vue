<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import * as postApi from '@/api/post'
import * as neighborApi from '@/api/neighbor'
import PostCard from '@/components/PostCard.vue'
import TagChip from '@/components/TagChip.vue'
import AppButton from '@/components/AppButton.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const posts = ref([])
const categories = ref([])
const tagCloud = ref([])
const blog = ref(null)
const loading = ref(false)
const page = ref(1)
const hasNext = ref(false)
const neighborType = ref(null)
const neighborLoading = ref(false)

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
    <header class="blog-header" v-if="blog">
      <div class="cover" :style="blog.coverImageUrl ? { backgroundImage: `url(${blog.coverImageUrl})` } : null" />
      <h1>{{ blog.blogName }}</h1>
      <p>{{ blog.intro }}</p>
      <AppButton v-if="isOwner()" @click="router.push('/write')">글쓰기</AppButton>
      <AppButton
        v-if="canManageNeighbor()"
        :variant="neighborType ? 'ghost' : 'primary'"
        :loading="neighborLoading"
        @click="toggleNeighbor"
      >
        {{ neighborType === 'MUTUAL' ? '서로이웃 · 삭제' : neighborType === 'ONE_SIDED' ? '이웃 삭제' : '이웃 추가' }}
      </AppButton>
    </header>

    <div class="layout">
      <aside class="sidebar">
        <h3>카테고리</h3>
        <ul class="category-tree">
          <li>
            <button type="button" @click="filterByCategory(undefined)">전체</button>
          </li>
          <li v-for="c in categories" :key="c.id">
            <button type="button" :class="{ active: String(route.query.categoryId) === String(c.id) }" @click="filterByCategory(c.id)">
              {{ c.name }} ({{ c.postCount }})
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
        <div class="grid">
          <PostCard v-for="p in posts" :key="p.id" :post="p" />
        </div>
        <div class="load-more" v-if="hasNext">
          <AppButton variant="ghost" :loading="loading" @click="loadMore">더 보기</AppButton>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.blog-home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 16px 64px;
}
.blog-header {
  text-align: center;
  margin-bottom: 32px;
}
.cover {
  height: 160px;
  border-radius: var(--radius);
  background: linear-gradient(135deg, #E3F0FF, #FFE9E3);
  background-size: cover;
  background-position: center;
  border: 2px solid var(--color-border);
  margin-bottom: 16px;
}
.layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}
@media (min-width: 768px) {
  .layout {
    grid-template-columns: 220px 1fr;
  }
}
.sidebar h3 {
  font-size: 16px;
  margin: 16px 0 8px;
}
.category-tree {
  list-style: none;
  padding: 0;
  margin: 0;
}
.category-tree button {
  background: none;
  border: none;
  padding: 6px 0;
  font-size: 14px;
  cursor: pointer;
  color: var(--color-ink);
}
.category-tree button.active {
  color: var(--color-primary-dark);
  font-weight: 700;
}
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}
@media (min-width: 768px) {
  .grid { grid-template-columns: 1fr 1fr; }
}
@media (min-width: 1280px) {
  .grid { grid-template-columns: 1fr 1fr 1fr; }
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
