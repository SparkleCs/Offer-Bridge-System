<template>
  <el-container class="app-shell">
    <el-header class="topbar">
      <div class="topbar-inner">
        <button class="brand" @click="go('/')">
          <span class="brand-dot"></span>
          <span>OfferBridge</span>
        </button>

        <el-menu :default-active="route.path" mode="horizontal" class="main-menu" @select="go">
          <el-menu-item index="/agencies">中介筛选</el-menu-item>
          <el-menu-item index="/me">个人中心</el-menu-item>
          <el-menu-item index="/forum">论坛</el-menu-item>
          <el-menu-item index="/orders">订单</el-menu-item>
          <el-menu-item index="/universities">世界大学库</el-menu-item>
        </el-menu>

        <div class="auth-area">
          <template v-if="authStore.isLoggedIn">
            <el-dropdown trigger="click">
              <div class="avatar-trigger">
                <el-avatar :size="34">{{ userInitial }}</el-avatar>
                <span class="user-name">{{ displayName }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="go('/me')">进入个人中心</el-dropdown-item>
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

    <el-main class="main-content">
      <router-view />
    </el-main>

    <el-footer class="footer">© {{ year }} OfferBridge 留学中介筛选与申请平台</el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const year = new Date().getFullYear()
const displayName = computed(() => authStore.profile?.name || '学生用户')
const userInitial = computed(() => displayName.value.slice(0, 1) || 'U')

function go(path: string) {
  if (path && path !== route.path) {
    router.push(path)
  }
}

async function handleLogout() {
  await authStore.logoutAll()
  router.push('/auth')
}
</script>
