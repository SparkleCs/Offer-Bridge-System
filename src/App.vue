<template>
  <template v-if="isBackofficeRoute">
    <router-view />
  </template>
  <el-container v-else class="app-shell">
    <el-header class="topbar" :class="{ 'topbar--home': route.path === '/' }">
      <div class="topbar-inner">
        <button class="brand" @click="go('/')">
          <span class="brand-dot"></span>
          <span>OfferBridge</span>
        </button>

        <el-menu :default-active="route.path" mode="horizontal" class="main-menu" @select="go">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/agencies">中介</el-menu-item>
          <el-menu-item v-if="showAgencyCenter" index="/org-admin/verification">机构管理</el-menu-item>
          <el-menu-item v-if="showWorkbench" index="/agent-workbench/communication">中介工作台</el-menu-item>
          <el-menu-item index="/universities">院校</el-menu-item>
          <el-menu-item index="/forum">论坛</el-menu-item>
        </el-menu>

        <div class="auth-area">
          <template v-if="authStore.isLoggedIn">
            <el-badge :value="messageUnreadTotal" :hidden="messageUnreadTotal === 0" class="msg-badge">
              <el-button text class="msg-btn" @click="go('/messages')">消息</el-button>
            </el-badge>
            <el-dropdown trigger="click">
              <div class="avatar-trigger">
                <el-avatar :size="34">{{ userInitial }}</el-avatar>
                <span class="user-name">{{ displayName }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="go('/me')">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="currentRole === 'STUDENT'" @click="go('/agency-favorites')">中介收藏</el-dropdown-item>
                  <el-dropdown-item v-if="currentRole === 'STUDENT'" @click="go('/orders')">订单管理</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" round @click="go('/auth')">登录/注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <el-main class="main-content" :class="{ 'main-content--home': route.path === '/' }">
      <router-view />
    </el-main>

    <el-footer class="footer">© {{ year }} OfferBridge 留学中介筛选与申请平台</el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { listForumNotifications } from './services/forum'
import { getChatUnreadSummary, listSystemNotifications } from './services/message'
import { useAuthStore } from './stores/auth'
import { confirmLogout } from './utils/logoutConfirm'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const year = new Date().getFullYear()
const currentRole = computed(() => authStore.authMeta?.role || '')
const showAgencyCenter = computed(() => currentRole.value === 'AGENT_ORG')
const showWorkbench = computed(() => currentRole.value === 'AGENT_MEMBER')
const isBackofficeRoute = computed(() => route.path.startsWith('/org-admin') || route.path.startsWith('/agent-workbench') || route.path.startsWith('/admin'))
const messageUnreadTotal = ref(0)
const displayName = computed(() => {
  if (authStore.profile?.name) return authStore.profile.name
  const role = currentRole.value
  if (role === 'AGENT_ORG') return '机构管理员'
  if (role === 'AGENT_MEMBER') return '机构成员'
  if (role === 'ADMIN') return '平台管理员'
  return '学生用户'
})
const userInitial = computed(() => displayName.value.slice(0, 1) || 'U')

function handleUnreadEvent(event: Event) {
  const detail = (event as CustomEvent<{ total?: number }>).detail
  if (typeof detail?.total === 'number') {
    messageUnreadTotal.value = Math.max(0, detail.total)
  }
}

function go(path: string) {
  if (path && path !== route.path) {
    router.push(path)
  }
}

async function refreshMessageUnreadTotal() {
  if (!authStore.isLoggedIn) {
    messageUnreadTotal.value = 0
    return
  }
  try {
    const [forum, system, chat] = await Promise.all([
      listForumNotifications({ page: 1, pageSize: 1 }),
      listSystemNotifications({ page: 1, pageSize: 1 }),
      getChatUnreadSummary()
    ])
    messageUnreadTotal.value = (forum.unreadCount || 0) + (system.unreadCount || 0) + (chat.unreadCount || 0)
  } catch {
    messageUnreadTotal.value = 0
  }
}

async function handleLogout() {
  if (!(await confirmLogout())) return
  await authStore.logoutAll()
  messageUnreadTotal.value = 0
  router.push('/auth')
}

watch(
  () => authStore.isLoggedIn,
  () => {
    refreshMessageUnreadTotal()
  },
  { immediate: true }
)

watch(
  () => route.fullPath,
  () => {
    if (authStore.isLoggedIn) {
      refreshMessageUnreadTotal()
    }
  }
)

onMounted(() => {
  window.addEventListener('offerbridge:message-unread-change', handleUnreadEvent)
})

onBeforeUnmount(() => {
  window.removeEventListener('offerbridge:message-unread-change', handleUnreadEvent)
})
</script>

<style scoped>
.msg-badge {
  margin-right: 8px;
}

.msg-btn {
  font-weight: 600;
}
</style>
