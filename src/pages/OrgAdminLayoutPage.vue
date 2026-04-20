<template>
  <div class="system-shell">
    <aside class="side-nav">
      <div class="brand">机构管理端</div>
      <el-menu :default-active="route.path" class="menu" @select="go">
        <el-menu-item index="/org-admin/verification">公司认证</el-menu-item>
        <el-menu-item index="/org-admin/members">员工管理</el-menu-item>
        <el-menu-item index="/org-admin/permissions">权限管理</el-menu-item>
        <el-menu-item index="/org-admin/company">公司信息</el-menu-item>
      </el-menu>
      <el-button class="back-btn" text @click="router.push('/')">返回主站</el-button>
    </aside>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

function go(path: string) {
  if (path !== route.path) {
    router.push(path)
  }
}
</script>

<style scoped>
.system-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 240px 1fr;
  background: #f2f5fb;
}

.side-nav {
  background: linear-gradient(180deg, #10253f 0%, #173a57 100%);
  color: #fff;
  padding: 20px 14px;
  display: flex;
  flex-direction: column;
}

.brand {
  font-size: 18px;
  font-weight: 700;
  padding: 8px 10px 18px;
}

.menu {
  border-right: none;
  background: transparent;
  flex: 1;
}

.menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.86);
  border-radius: 10px;
  margin-bottom: 6px;
}

.menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: rgba(39, 167, 232, 0.35);
}

.back-btn {
  color: #d3e5f4;
}

.content {
  padding: 20px;
}

@media (max-width: 900px) {
  .system-shell {
    grid-template-columns: 1fr;
  }

  .side-nav {
    min-height: auto;
  }
}
</style>
