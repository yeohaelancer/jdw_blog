<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import * as adminApi from '@/api/admin'

const authStore = useAuthStore()
const users = ref([])
const errorMessage = ref('')

async function load() {
  const res = await adminApi.fetchAllUsers()
  users.value = res.data
}

async function toggleStatus(user) {
  const nextStatus = user.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE'
  await adminApi.updateUserStatus(user.id, nextStatus)
  await load()
}

async function toggleRole(user) {
  errorMessage.value = ''
  const nextRole = user.role === 'ADMIN' ? 'USER' : 'ADMIN'
  if (nextRole === 'USER' && !confirm(`${user.nickname} 님의 관리자 권한을 해제할까요?`)) return
  if (nextRole === 'ADMIN' && !confirm(`${user.nickname} 님을 관리자로 지정할까요?`)) return
  try {
    await adminApi.updateUserRole(user.id, nextRole)
    await load()
  } catch (e) {
    errorMessage.value = e?.response?.data?.message || '권한 변경에 실패했습니다.'
  }
}

onMounted(load)
</script>

<template>
  <div class="admin-view">
    <h1>회원 관리</h1>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <table class="user-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>이메일</th>
          <th>닉네임</th>
          <th>권한</th>
          <th>상태</th>
          <th>관리</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.id }}</td>
          <td>{{ u.email }}</td>
          <td>{{ u.nickname }}</td>
          <td>
            <span class="role" :class="u.role.toLowerCase()">{{ u.role }}</span>
          </td>
          <td>
            <span class="status" :class="u.status.toLowerCase()">{{ u.status }}</span>
          </td>
          <td class="actions">
            <button type="button" class="link-btn" @click="toggleStatus(u)">
              {{ u.status === 'ACTIVE' ? '정지' : '활성화' }}
            </button>
            <button
              type="button"
              class="link-btn"
              :disabled="u.id === authStore.userId && u.role === 'ADMIN'"
              @click="toggleRole(u)"
            >
              {{ u.role === 'ADMIN' ? '관리자 해제' : '관리자로 지정' }}
            </button>
          </td>
        </tr>
        <tr v-if="users.length === 0">
          <td colspan="6" class="empty">회원이 없습니다.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.admin-view {
  max-width: 860px;
  margin: 0 auto;
  padding: 24px 16px 64px;
}
.error { color: var(--color-error); margin-bottom: 12px; }
.user-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  overflow: hidden;
}
.user-table th, .user-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
  font-size: 14px;
}
.role {
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
}
.role.admin { background: #FCF4DD; color: #8A6D00; }
.role.user { background: #eee; color: var(--color-ink-soft); }
.status {
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
}
.status.active { background: #E3F7E3; color: #2E8B57; }
.status.suspended { background: #FDEDEB; color: var(--color-error); }
.status.withdrawn { background: #eee; color: var(--color-ink-soft); }
.actions { display: flex; gap: 12px; }
.link-btn {
  background: none;
  border: none;
  color: var(--color-primary-dark);
  cursor: pointer;
  padding: 0;
  font-size: 13px;
}
.link-btn:disabled { color: var(--color-ink-soft); cursor: not-allowed; }
.empty { text-align: center; color: var(--color-ink-soft); }
</style>
