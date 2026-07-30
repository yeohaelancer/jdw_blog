<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()
const router = useRouter()

function onLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div id="app-wrapper">
    <header class="app-header">
      <RouterLink to="/" class="logo">물빛 다이어리</RouterLink>
      <nav>
        <RouterLink to="/explore">전체 글</RouterLink>
        <template v-if="authStore.isLoggedIn">
          <span class="nickname">{{ authStore.nickname }} 님</span>
          <RouterLink :to="`/blogs/${authStore.blogId}`">내 블로그</RouterLink>
          <RouterLink to="/neighbors">내 이웃</RouterLink>
          <RouterLink v-if="authStore.isAdmin" to="/admin">관리자</RouterLink>
          <button type="button" class="link-btn" @click="onLogout">로그아웃</button>
        </template>
        <template v-else>
          <RouterLink to="/login">로그인</RouterLink>
          <RouterLink to="/signup">회원가입</RouterLink>
        </template>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 2px solid var(--color-border);
  background: var(--color-surface);
}
.logo {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-ink);
  text-decoration: none;
}
nav {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
}
nav a {
  color: var(--color-ink);
  text-decoration: none;
}
.nickname {
  color: var(--color-ink-soft);
}
.link-btn {
  background: none;
  border: none;
  color: var(--color-primary-dark);
  cursor: pointer;
  font-size: 14px;
  padding: 0;
}
</style>
