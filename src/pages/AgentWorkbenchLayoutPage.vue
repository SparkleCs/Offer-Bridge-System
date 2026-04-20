<template>
  <div class="system-shell">
    <aside class="side-nav">
      <div class="brand">中介工作台</div>
      <el-menu :default-active="route.path" class="menu" @select="go">
        <el-menu-item index="/agent-workbench/team-products">团队产品管理</el-menu-item>
        <el-menu-item index="/agent-workbench/recommend">推荐学生</el-menu-item>
        <el-menu-item index="/agent-workbench/search">搜索</el-menu-item>
        <el-menu-item index="/agent-workbench/communication">沟通</el-menu-item>
        <el-menu-item index="/agent-workbench/profile">个人信息</el-menu-item>
        <el-menu-item index="/agent-workbench/data">数据</el-menu-item>
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
  background: #f3f6fb;
}

.side-nav {
  background: linear-gradient(180deg, #072636 0%, #0f3f53 100%);
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
  background: rgba(16, 206, 178, 0.28);
}

.back-btn {
  color: #d3f0eb;
}

.content {
  padding: 20px;
}

@media (max-width: 900px) {
  .system-shell {
    grid-template-columns: 1fr;
  }
}
</style>
