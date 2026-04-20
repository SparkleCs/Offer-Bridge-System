<template>
  <div class="admin-shell">
    <aside class="side-nav">
      <div class="brand-card">
        <el-avatar :size="52">管</el-avatar>
        <div>
          <div class="title">平台管理员</div>
          <div class="sub">Review Console</div>
        </div>
      </div>

      <el-menu :default-active="route.path" class="menu" @select="go">
        <el-menu-item index="/admin/org-reviews"><el-icon><OfficeBuilding /></el-icon><span>公司审核</span></el-menu-item>
        <el-menu-item index="/admin/member-reviews"><el-icon><UserFilled /></el-icon><span>员工审核</span></el-menu-item>
        <el-menu-item index="/admin/student-reviews"><el-icon><Reading /></el-icon><span>学生审核</span></el-menu-item>
        <el-menu-item index="/admin/notifications"><el-icon><Bell /></el-icon><span>通知记录</span></el-menu-item>
      </el-menu>

      <el-button text class="logout-btn" @click="logout">退出登录</el-button>
    </aside>

    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { Bell, OfficeBuilding, Reading, UserFilled } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

function go(path: string) {
  if (path !== route.path) router.push(path)
}

async function logout() {
  await authStore.logoutAll()
  router.push('/admin-auth')
}
</script>

<style scoped>
.admin-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 250px 1fr;
  background: #f3f7fb;
}

.side-nav {
  background: linear-gradient(180deg, #16283f 0%, #1f3f66 100%);
  padding: 14px;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.brand-card {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 14px;
  border: 1px solid rgba(180, 219, 255, 0.25);
  background: rgba(255, 255, 255, 0.08);
  padding: 10px;
  margin-bottom: 14px;
}

.title {
  font-size: 16px;
  font-weight: 700;
}

.sub {
  font-size: 12px;
  color: rgba(220, 237, 255, 0.76);
}

.menu {
  background: transparent;
  flex: 1;
}

.menu :deep(.el-menu) {
  border-right: none;
  background: transparent;
}

.menu :deep(.el-menu-item) {
  color: rgba(242, 249, 255, 0.9);
  border-radius: 10px;
  margin-bottom: 7px;
  transition: all 0.2s ease;
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.14);
  transform: translateX(2px);
}

.menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: rgba(41, 130, 224, 0.8);
  box-shadow: 0 8px 16px rgba(6, 26, 49, 0.4);
}

.logout-btn {
  width: 100%;
  justify-content: center;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  color: #e8f2fb;
}

.content {
  padding: 20px;
}

@media (max-width: 980px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }
}
</style>
