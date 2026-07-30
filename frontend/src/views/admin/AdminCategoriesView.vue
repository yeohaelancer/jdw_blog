<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import * as postApi from '@/api/post'
import WatercolorCard from '@/components/WatercolorCard.vue'
import AppButton from '@/components/AppButton.vue'

const authStore = useAuthStore()
const categories = ref([])
const newName = ref('')
const editingId = ref(null)
const editingName = ref('')
const errorMessage = ref('')
const saving = ref(false)

async function load() {
  const res = await postApi.fetchCategories(authStore.blogId)
  categories.value = res.data
}

async function addCategory() {
  if (!newName.value.trim()) return
  errorMessage.value = ''
  saving.value = true
  try {
    await postApi.createCategory(authStore.blogId, { name: newName.value.trim(), sortOrder: categories.value.length + 1 })
    newName.value = ''
    await load()
  } catch (e) {
    errorMessage.value = e?.response?.data?.message || '카테고리 생성에 실패했습니다.'
  } finally {
    saving.value = false
  }
}

function startEdit(category) {
  editingId.value = category.id
  editingName.value = category.name
}

function cancelEdit() {
  editingId.value = null
  editingName.value = ''
}

async function saveEdit(category) {
  if (!editingName.value.trim()) return
  await postApi.updateCategory(authStore.blogId, category.id, {
    name: editingName.value.trim(),
    sortOrder: category.sortOrder
  })
  cancelEdit()
  await load()
}

async function removeCategory(category) {
  await postApi.deleteCategory(authStore.blogId, category.id)
  await load()
}

onMounted(load)
</script>

<template>
  <div class="admin-view">
    <h1>카테고리 관리</h1>

    <WatercolorCard class="add-form">
      <form @submit.prevent="addCategory">
        <input v-model="newName" placeholder="새 카테고리 이름 (예: 낚시)" maxlength="100" />
        <AppButton type="submit" size="sm" :loading="saving">추가</AppButton>
      </form>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </WatercolorCard>

    <table class="category-table">
      <thead>
        <tr>
          <th>순서</th>
          <th>이름</th>
          <th>게시글 수</th>
          <th>관리</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in categories" :key="c.id">
          <td>{{ c.sortOrder }}</td>
          <td>
            <input v-if="editingId === c.id" v-model="editingName" />
            <span v-else>{{ c.name }}</span>
          </td>
          <td>{{ c.postCount }}</td>
          <td class="actions">
            <template v-if="editingId === c.id">
              <button type="button" class="link-btn" @click="saveEdit(c)">저장</button>
              <button type="button" class="link-btn" @click="cancelEdit">취소</button>
            </template>
            <template v-else>
              <button type="button" class="link-btn" @click="startEdit(c)">수정</button>
              <button type="button" class="link-btn danger" @click="removeCategory(c)">삭제</button>
            </template>
          </td>
        </tr>
        <tr v-if="categories.length === 0">
          <td colspan="4" class="empty">등록된 카테고리가 없습니다.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.admin-view {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px 16px 64px;
}
.add-form form {
  display: flex;
  gap: 12px;
}
.add-form input {
  flex: 1;
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 8px 12px;
}
.error { color: var(--color-error); margin: 8px 0 0; }
.category-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 24px;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  overflow: hidden;
}
.category-table th, .category-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}
.category-table input {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 4px 8px;
}
.actions { display: flex; gap: 10px; }
.link-btn {
  background: none;
  border: none;
  color: var(--color-primary-dark);
  cursor: pointer;
  padding: 0;
  font-size: 13px;
}
.link-btn.danger { color: var(--color-error); }
.empty { text-align: center; color: var(--color-ink-soft); }
</style>
