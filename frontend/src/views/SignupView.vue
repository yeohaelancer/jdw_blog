<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import * as authApi from '@/api/auth'
import WatercolorCard from '@/components/WatercolorCard.vue'
import AppButton from '@/components/AppButton.vue'

const email = ref('')
const nickname = ref('')
const password = ref('')
const passwordConfirm = ref('')
const emailAvailable = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const router = useRouter()
const authStore = useAuthStore()

watch(email, () => {
  emailAvailable.value = null
})

async function checkEmail() {
  if (!email.value) return
  try {
    const res = await authApi.checkEmail(email.value)
    emailAvailable.value = res.data.available
  } catch {
    emailAvailable.value = null
  }
}

async function onSubmit() {
  errorMessage.value = ''
  if (password.value !== passwordConfirm.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }
  loading.value = true
  try {
    await authStore.signup({ email: email.value, password: password.value, nickname: nickname.value })
    router.push('/')
  } catch (e) {
    errorMessage.value = e?.response?.data?.message || '회원가입에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <WatercolorCard tint="coral" class="auth-card">
      <h1 class="logo">회원가입</h1>
      <form @submit.prevent="onSubmit">
        <label>
          이메일
          <input v-model="email" type="email" required @blur="checkEmail" />
          <span v-if="emailAvailable === true" class="hint ok">사용 가능한 이메일입니다.</span>
          <span v-if="emailAvailable === false" class="hint error">이미 사용 중인 이메일입니다.</span>
        </label>
        <label>
          닉네임
          <input v-model="nickname" type="text" required minlength="2" maxlength="50" />
        </label>
        <label>
          비밀번호 (8자 이상, 영문+숫자)
          <input v-model="password" type="password" required minlength="8" />
        </label>
        <label>
          비밀번호 확인
          <input v-model="passwordConfirm" type="password" required />
        </label>
        <p v-if="errorMessage" class="hint error">{{ errorMessage }}</p>
        <AppButton type="submit" :loading="loading" style="width: 100%; justify-content: center">
          회원가입
        </AppButton>
      </form>
      <p class="switch">
        이미 계정이 있으신가요? <RouterLink to="/login">로그인</RouterLink>
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
.hint {
  font-size: 12px;
}
.hint.ok { color: #2E8B57; }
.hint.error, .error { color: var(--color-error); margin: 0; }
.switch {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}
</style>
