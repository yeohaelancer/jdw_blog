<script setup>
import { ref, onMounted } from 'vue'
import * as adminApi from '@/api/admin'

const posts = ref([])

async function load() {
  const res = await adminApi.fetchAllPosts()
  posts.value = res.data
}

async function removePost(post) {
  await adminApi.deletePostAsAdmin(post.id)
  await load()
}

onMounted(load)
</script>

<template>
  <div class="admin-view">
    <h1>게시글 관리</h1>
    <table class="post-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>블로그</th>
          <th>작성자</th>
          <th>제목</th>
          <th>공개범위</th>
          <th>상태</th>
          <th>작성일</th>
          <th>관리</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in posts" :key="p.id">
          <td>{{ p.id }}</td>
          <td>{{ p.blogName }}</td>
          <td>{{ p.authorNickname }}</td>
          <td>
            <RouterLink :to="`/posts/${p.id}`">{{ p.title }}</RouterLink>
          </td>
          <td>{{ p.visibility }}</td>
          <td>{{ p.status }}</td>
          <td>{{ new Date(p.createdAt).toLocaleDateString() }}</td>
          <td>
            <button type="button" class="link-btn danger" @click="removePost(p)">삭제</button>
          </td>
        </tr>
        <tr v-if="posts.length === 0">
          <td colspan="8" class="empty">게시글이 없습니다.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.admin-view {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px 16px 64px;
}
.post-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  overflow: hidden;
  font-size: 13px;
}
.post-table th, .post-table td {
  padding: 8px 10px;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}
.link-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  font-size: 13px;
}
.link-btn.danger { color: var(--color-error); }
.empty { text-align: center; color: var(--color-ink-soft); }
</style>
