<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="admin-layout">
    <nav class="admin-nav">
      <RouterLink to="/admin/categories">카테고리 관리</RouterLink>
      <RouterLink to="/admin/users">회원 관리</RouterLink>
      <RouterLink to="/admin/posts">게시글 관리</RouterLink>
    </nav>
    <RouterView />
  </div>
</template>

<style scoped>
.admin-nav {
  display: flex;
  gap: 20px;
  justify-content: center;
  padding: 16px;
  border-bottom: 2px solid var(--color-border);
  background: var(--color-surface);
}
.admin-nav a {
  color: var(--color-ink);
  text-decoration: none;
  font-family: var(--font-heading);
  font-size: 15px;
  padding: 4px 8px;
}
.admin-nav a.router-link-active {
  color: var(--color-primary-dark);
  border-bottom: 2px solid var(--color-primary-dark);
}
</style>
