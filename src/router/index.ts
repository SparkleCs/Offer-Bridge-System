import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('../pages/HomePage.vue') },
  { path: '/auth', name: 'auth', component: () => import('../pages/AuthPage.vue') },
  { path: '/admin-auth', name: 'admin-auth', component: () => import('../pages/AdminAuthPage.vue') },
  { path: '/verification', name: 'verification', component: () => import('../pages/VerificationPage.vue'), meta: { requiresAuth: true } },
  { path: '/login', redirect: '/auth' },
  { path: '/register', redirect: '/auth' },
  { path: '/agencies', name: 'agencies', component: () => import('../pages/AgenciesPage.vue') },
  {
    path: '/admin',
    component: () => import('../pages/AdminLayoutPage.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ADMIN'] },
    children: [
      { path: '', redirect: '/admin/org-reviews' },
      { path: 'org-reviews', name: 'admin-org-reviews', component: () => import('../pages/AdminOrgReviewsPage.vue') },
      { path: 'member-reviews', name: 'admin-member-reviews', component: () => import('../pages/AdminMemberReviewsPage.vue') },
      { path: 'student-reviews', name: 'admin-student-reviews', component: () => import('../pages/AdminStudentReviewsPage.vue') },
      { path: 'notifications', name: 'admin-notifications', component: () => import('../pages/AdminNotificationsPage.vue') }
    ]
  },
  {
    path: '/org-admin',
    component: () => import('../pages/OrgAdminLayoutPage.vue'),
    meta: { requiresAuth: true, allowedRoles: ['AGENT_ORG'] },
    children: [
      { path: '', redirect: '/org-admin/verification' },
      { path: 'verification', name: 'org-admin-verification', component: () => import('../pages/OrgVerificationPage.vue') },
      { path: 'members', name: 'org-admin-members', component: () => import('../pages/OrgMembersPage.vue') },
      { path: 'permissions', name: 'org-admin-permissions', component: () => import('../pages/OrgPermissionsPage.vue') },
      { path: 'company', name: 'org-admin-company', component: () => import('../pages/OrgCompanyPage.vue') }
    ]
  },
  {
    path: '/agent-workbench',
    component: () => import('../pages/AgentWorkbenchLayoutPage.vue'),
    meta: { requiresAuth: true, allowedRoles: ['AGENT_MEMBER'] },
    children: [
      { path: '', redirect: '/agent-workbench/communication' },
      { path: 'team-products', name: 'agent-team-products', component: () => import('../pages/AgentTeamProductsPage.vue') },
      { path: 'students', name: 'agent-students', component: () => import('../pages/AgentStudentManagementPage.vue') },
      { path: 'recommend', name: 'agent-recommend', component: () => import('../pages/AgentRecommendStudentsPage.vue') },
      { path: 'search', name: 'agent-search', component: () => import('../pages/AgentSearchPage.vue') },
      { path: 'communication', name: 'agent-communication', component: () => import('../pages/AgentCommunicationPage.vue') },
      { path: 'profile', name: 'agent-profile', component: () => import('../pages/AgentProfilePage.vue') },
      { path: 'data', name: 'agent-data', component: () => import('../pages/AgentDataPage.vue') }
    ]
  },
  { path: '/agency-center', name: 'agency-center', component: () => import('../pages/AgencyCenterPage.vue'), meta: { requiresAuth: true, allowedRoles: ['AGENT_ORG', 'AGENT_MEMBER'] } },
  { path: '/messages', name: 'messages', component: () => import('../pages/MessagesPage.vue'), meta: { requiresAuth: true } },
  { path: '/me', name: 'me', component: () => import('../pages/MePage.vue'), meta: { requiresAuth: true } },
  { path: '/forum', name: 'forum', component: () => import('../pages/ForumPage.vue') },
  { path: '/forum/mine', name: 'forum-mine', component: () => import('../pages/ForumMinePage.vue'), meta: { requiresAuth: true } },
  { path: '/forum/new', name: 'forum-new', component: () => import('../pages/ForumEditorPage.vue'), meta: { requiresAuth: true, allowedRoles: ['STUDENT'] } },
  { path: '/forum/edit/:postId', name: 'forum-edit', component: () => import('../pages/ForumEditorPage.vue'), meta: { requiresAuth: true, allowedRoles: ['STUDENT'] } },
  { path: '/orders', name: 'orders', component: () => import('../pages/OrdersPage.vue'), meta: { requiresAuth: true } },
  { path: '/universities', name: 'universities', component: () => import('../pages/UniversitiesPage.vue') }
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

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const role = authStore.authMeta?.role

  if (role === 'AGENT_ORG') {
    if (to.path === '/auth') return '/org-admin/verification'
    if (to.path === '/admin-auth') return '/org-admin/verification'
    if (!to.path.startsWith('/org-admin')) return '/org-admin/verification'
    return true
  }
  if (role === 'AGENT_MEMBER') {
    if (to.path === '/auth') return '/agent-workbench/communication'
    if (to.path === '/admin-auth') return '/agent-workbench/communication'
    if (!to.path.startsWith('/agent-workbench')) return '/agent-workbench/communication'
    return true
  }
  if (role === 'ADMIN') {
    if (to.path === '/auth') return '/admin/org-reviews'
    if (!to.path.startsWith('/admin')) return '/admin/org-reviews'
    return true
  }

  const allowedRoles = to.meta?.allowedRoles as string[] | undefined
  if (!allowedRoles || allowedRoles.length === 0) return true

  if (!role || !allowedRoles.includes(role)) {
    return '/'
  }
  return true
})

export default router
