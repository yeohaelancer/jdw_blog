import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/BlogHomeView.vue')
    },
    {
      path: '/blogs/:blogId',
      name: 'blog-home',
      component: () => import('@/views/BlogHomeView.vue')
    },
    {
      path: '/explore',
      name: 'explore',
      component: () => import('@/views/ExploreView.vue')
    },
    {
      path: '/neighbors',
      name: 'neighbors',
      component: () => import('@/views/NeighborsView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/SignupView.vue')
    },
    {
      path: '/write',
      name: 'post-editor',
      component: () => import('@/views/PostEditorView.vue')
    },
    {
      path: '/posts/:id/edit',
      name: 'post-edit',
      component: () => import('@/views/PostEditorView.vue')
    },
    {
      path: '/posts/:id',
      name: 'post-detail',
      component: () => import('@/views/PostDetailView.vue')
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      meta: { requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/categories' },
        { path: 'categories', name: 'admin-categories', component: () => import('@/views/admin/AdminCategoriesView.vue') },
        { path: 'users', name: 'admin-users', component: () => import('@/views/admin/AdminUsersView.vue') },
        { path: 'posts', name: 'admin-posts', component: () => import('@/views/admin/AdminPostsView.vue') }
      ]
    }
  ]
})

router.beforeEach((to) => {
  if (to.meta.requiresAdmin) {
    const authStore = useAuthStore()
    if (!authStore.isAdmin) {
      return '/login'
    }
  }
  return true
})

export default router
