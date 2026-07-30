<script setup>
import { ref, onMounted } from 'vue'
import * as postApi from '@/api/post'
import PostCard from '@/components/PostCard.vue'
import AppButton from '@/components/AppButton.vue'

const posts = ref([])
const loading = ref(false)
const page = ref(1)
const hasNext = ref(false)
const keyword = ref('')

async function load(reset = true) {
  loading.value = true
  try {
    if (reset) {
      page.value = 1
      posts.value = []
    }
    const res = await postApi.fetchPosts({ keyword: keyword.value || undefined, page: page.value, size: 12 })
    posts.value = reset ? res.data.items : [...posts.value, ...res.data.items]
    hasNext.value = res.data.hasNext
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value += 1
  load(false)
}

onMounted(() => load(true))
</script>

<template>
  <div class="explore">
    <h1>전체 글 둘러보기</h1>
    <p class="subtitle">다른 회원들이 공개로 작성한 글을 모두 모아봤어요.</p>

    <form class="search" @submit.prevent="load(true)">
      <input v-model="keyword" placeholder="제목/내용으로 검색" />
      <AppButton type="submit" size="sm">검색</AppButton>
    </form>

    <p v-if="!loading && posts.length === 0" class="empty">아직 공개된 글이 없어요 🌊</p>
    <div class="grid">
      <PostCard v-for="p in posts" :key="p.id" :post="p" show-blog-link />
    </div>
    <div class="load-more" v-if="hasNext">
      <AppButton variant="ghost" :loading="loading" @click="loadMore">더 보기</AppButton>
    </div>
  </div>
</template>

<style scoped>
.explore {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 16px 64px;
}
.subtitle {
  color: var(--color-ink-soft);
  margin-top: 4px;
}
.search {
  display: flex;
  gap: 12px;
  margin: 24px 0;
  max-width: 480px;
}
.search input {
  flex: 1;
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 8px 12px;
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
