<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import * as neighborApi from '@/api/neighbor'
import Avatar from '@/components/Avatar.vue'

const router = useRouter()
const authStore = useAuthStore()
const neighbors = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res = await neighborApi.fetchMyNeighbors()
    neighbors.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  load()
})
</script>

<template>
  <div class="neighbors">
    <h1>내 이웃</h1>
    <p v-if="!loading && neighbors.length === 0" class="empty">
      아직 추가한 이웃이 없어요. <RouterLink to="/explore">전체 글 둘러보기</RouterLink>에서 마음에 드는 블로그를 찾아보세요.
    </p>
    <ul class="neighbor-list">
      <li v-for="n in neighbors" :key="n.blogId">
        <RouterLink :to="`/blogs/${n.blogId}`" class="neighbor-item">
          <Avatar :src="n.profileImageUrl" size="md" />
          <span class="name">{{ n.blogName }}</span>
          <span class="badge" :class="n.neighborType.toLowerCase()">
            {{ n.neighborType === 'MUTUAL' ? '서로이웃' : '이웃' }}
          </span>
        </RouterLink>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.neighbors {
  max-width: 640px;
  margin: 0 auto;
  padding: 24px 16px 64px;
}
.empty {
  color: var(--color-ink-soft);
  margin-top: 24px;
}
.neighbor-list {
  list-style: none;
  padding: 0;
  margin: 24px 0 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.neighbor-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  text-decoration: none;
  color: var(--color-ink);
}
.name {
  flex: 1;
  font-family: var(--font-heading);
}
.badge {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 999px;
}
.badge.mutual { background: #FCF4DD; color: #8A6D00; }
.badge.one_sided { background: #eee; color: var(--color-ink-soft); }
</style>
