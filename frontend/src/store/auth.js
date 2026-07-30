import { defineStore } from 'pinia'
import * as authApi from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    userId: localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null,
    blogId: localStorage.getItem('blogId') ? Number(localStorage.getItem('blogId')) : null,
    nickname: localStorage.getItem('nickname') || null,
    role: localStorage.getItem('role') || null
  }),
  getters: {
    isLoggedIn: (state) => !!state.userId,
    isAdmin: (state) => state.role === 'ADMIN'
  },
  actions: {
    setSession(tokenResult) {
      localStorage.setItem('accessToken', tokenResult.accessToken)
      localStorage.setItem('refreshToken', tokenResult.refreshToken)
      localStorage.setItem('userId', tokenResult.userId)
      localStorage.setItem('nickname', tokenResult.nickname)
      localStorage.setItem('role', tokenResult.role || 'USER')
      if (tokenResult.blogId) {
        localStorage.setItem('blogId', tokenResult.blogId)
      }
      this.userId = tokenResult.userId
      this.blogId = tokenResult.blogId
      this.nickname = tokenResult.nickname
      this.role = tokenResult.role || 'USER'
    },
    async login(payload) {
      const res = await authApi.login(payload)
      this.setSession(res.data)
      return res.data
    },
    async signup(payload) {
      const res = await authApi.signup(payload)
      this.setSession(res.data)
      return res.data
    },
    logout() {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userId')
      localStorage.removeItem('blogId')
      localStorage.removeItem('nickname')
      localStorage.removeItem('role')
      this.userId = null
      this.blogId = null
      this.nickname = null
      this.role = null
    }
  }
})
