<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { useDarkMode } from '@/composables/useDarkMode'

const authStore = useAuthStore()
const router = useRouter()
const { isDark, toggle } = useDarkMode()

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
        <div class="nav-links">
          <RouterLink to="/explore">전체 글</RouterLink>
          <RouterLink v-if="authStore.isLoggedIn" :to="`/blogs/${authStore.blogId}`">내 블로그</RouterLink>
          <RouterLink v-if="authStore.isLoggedIn" to="/neighbors">내 이웃</RouterLink>
          <RouterLink v-if="authStore.isAdmin" to="/admin">관리자</RouterLink>
          <RouterLink v-if="!authStore.isLoggedIn" to="/login">로그인</RouterLink>
          <RouterLink v-if="!authStore.isLoggedIn" to="/signup">회원가입</RouterLink>
          <span v-if="authStore.isLoggedIn" class="nickname">{{ authStore.nickname }} 님</span>
          <button v-if="authStore.isLoggedIn" type="button" class="link-btn" @click="onLogout">로그아웃</button>
        </div>
        <button
          type="button"
          class="dark-toggle"
          :class="{ on: isDark }"
          role="switch"
          :aria-checked="isDark"
          aria-label="다크모드 전환"
          @click="toggle"
        >
          <span class="dark-toggle-knob" />
        </button>
        <RouterLink v-if="authStore.isLoggedIn" to="/write" class="write-btn">글쓰기</RouterLink>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 40px;
  border-bottom: 1px solid var(--color-border);
  background: var(--color-surface);
}
.logo {
  font-family: var(--font-heading);
  font-size: 19px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--color-ink);
  text-decoration: none;
}
nav {
  display: flex;
  align-items: center;
  gap: 28px;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 22px;
  font-size: 14px;
  font-weight: 500;
}
nav a {
  color: var(--color-ink-soft);
  text-decoration: none;
}
nav a.router-link-active {
  color: var(--color-ink);
}
.nickname {
  color: var(--color-ink-soft);
  font-size: 14px;
}
.link-btn {
  background: none;
  border: none;
  color: var(--color-ink-soft);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  padding: 0;
}
.dark-toggle {
  width: 44px;
  height: 24px;
  border-radius: 12px;
  background: var(--toggle-track-off);
  position: relative;
  border: none;
  cursor: pointer;
  padding: 0;
  flex-shrink: 0;
  transition: background 0.2s;
}
.dark-toggle.on {
  background: var(--color-primary);
}
.dark-toggle-knob {
  width: 18px;
  height: 18px;
  border-radius: 9999px;
  background: #fff;
  position: absolute;
  top: 3px;
  left: 3px;
  transition: left 0.2s;
}
.dark-toggle.on .dark-toggle-knob {
  left: 23px;
}
.write-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 9999px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
}
</style>
