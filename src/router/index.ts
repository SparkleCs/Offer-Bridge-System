import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('../pages/HomePage.vue') },
  { path: '/auth', name: 'auth', component: () => import('../pages/AuthPage.vue') },
  { path: '/verification', name: 'verification', component: () => import('../pages/VerificationPage.vue'), meta: { requiresAuth: true } },
  { path: '/login', redirect: '/auth' },
  { path: '/register', redirect: '/auth' },
  { path: '/agencies', name: 'agencies', component: () => import('../pages/AgenciesPage.vue') },
  { path: '/me', name: 'me', component: () => import('../pages/MePage.vue'), meta: { requiresAuth: true } },
  { path: '/forum', name: 'forum', component: () => import('../pages/ForumPage.vue') },
  { path: '/orders', name: 'orders', component: () => import('../pages/OrdersPage.vue'), meta: { requiresAuth: true } },
  { path: '/universities', name: 'universities', component: () => import('../pages/UniversitiesPage.vue'), meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  const requiresAuth = Boolean(to.meta?.requiresAuth)
  if (!requiresAuth) return true

  if (authStore.isLoggedIn) return true

  const refreshed = await authStore.refreshSession()
  if (refreshed) return true
  return '/auth'
})

export default router
