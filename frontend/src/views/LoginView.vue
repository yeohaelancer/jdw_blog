<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import WatercolorCard from '@/components/WatercolorCard.vue'
import AppButton from '@/components/AppButton.vue'

const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')
const router = useRouter()
const authStore = useAuthStore()

async function onSubmit() {
  errorMessage.value = ''
  loading.value = true
  try {
    await authStore.login({ email: email.value, password: password.value })
    router.push('/')
  } catch (e) {
    errorMessage.value = e?.response?.data?.message || '이메일 또는 비밀번호가 올바르지 않습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <WatercolorCard tint="blue" class="auth-card">
      <h1 class="logo">물빛 다이어리</h1>
      <form @submit.prevent="onSubmit">
        <label>
          이메일
          <input v-model="email" type="email" required />
        </label>
        <label>
          비밀번호
          <input v-model="password" type="password" required />
        </label>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <AppButton type="submit" :loading="loading" style="width: 100%; justify-content: center">
          로그인
        </AppButton>
      </form>
      <p class="switch">
        계정이 없으신가요? <RouterLink to="/signup">회원가입</RouterLink>
      </p>
    </WatercolorCard>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  padding: 64px 16px;
}
.auth-card {
  width: 100%;
  max-width: 420px;
}
.logo {
  text-align: center;
  margin: 0 0 24px;
}
form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: var(--color-ink-soft);
}
input {
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 15px;
  font-family: var(--font-body);
}
.error {
  color: var(--color-error);
  font-size: 13px;
  margin: 0;
}
.switch {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}
</style>
