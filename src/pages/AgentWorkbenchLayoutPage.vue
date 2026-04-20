<template>
  <div class="system-shell work-shell">
    <aside class="side-nav">
      <div class="profile-card">
        <el-avatar :size="54" :src="myProfile?.avatarUrl || undefined" class="profile-avatar">{{ profileInitial }}</el-avatar>
        <div class="profile-meta">
          <div class="profile-title">{{ profileName }}</div>
          <div class="profile-subtitle">Consultant Workspace</div>
        </div>
        <el-upload :show-file-list="false" :http-request="onUploadAvatar" accept="image/*" class="profile-upload">
          <el-button size="small" :loading="uploadingAvatar" class="upload-btn">更换头像</el-button>
        </el-upload>
      </div>

      <div class="nav-divider"></div>

      <el-menu :default-active="route.path" class="menu" @select="go">
        <el-menu-item index="/agent-workbench/team-products">
          <el-icon class="menu-icon"><Grid /></el-icon>
          <span>团队产品管理</span>
        </el-menu-item>
        <el-menu-item index="/agent-workbench/recommend">
          <el-icon class="menu-icon"><Star /></el-icon>
          <span>推荐学生</span>
        </el-menu-item>
        <el-menu-item index="/agent-workbench/search">
          <el-icon class="menu-icon"><Search /></el-icon>
          <span>搜索</span>
        </el-menu-item>
        <el-menu-item index="/agent-workbench/communication">
          <el-icon class="menu-icon"><ChatDotRound /></el-icon>
          <span>沟通</span>
        </el-menu-item>
        <el-menu-item index="/agent-workbench/profile">
          <el-icon class="menu-icon"><UserFilled /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="/agent-workbench/data">
          <el-icon class="menu-icon"><DataAnalysis /></el-icon>
          <span>数据</span>
        </el-menu-item>
      </el-menu>

      <el-button class="logout-btn" text @click="handleLogout">退出登录</el-button>
    </aside>

    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, type UploadRequestOptions } from 'element-plus'
import { ChatDotRound, DataAnalysis, Grid, Search, Star, UserFilled } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getMyAgencyProfile, updateMyAvatar, uploadFile } from '../services/agency'
import type { MemberSelfProfile } from '../types/agency'
import { getUploadErrorMessage, validateUploadFileSize } from '../utils/upload'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const myProfile = ref<MemberSelfProfile | null>(null)
const uploadingAvatar = ref(false)

const profileName = computed(() => myProfile.value?.displayName || '机构成员')
const profileInitial = computed(() => (profileName.value?.slice(0, 1) || '顾').toUpperCase())

function go(path: string) {
  if (path !== route.path) {
    router.push(path)
  }
}

async function loadProfile() {
  try {
    myProfile.value = await getMyAgencyProfile()
  } catch {
    myProfile.value = null
  }
}

async function onUploadAvatar(options: UploadRequestOptions) {
  const file = options.file as File
  if (!validateUploadFileSize(file)) {
    return
  }
  uploadingAvatar.value = true
  try {
    const uploaded = await uploadFile(file, 'avatar')
    await updateMyAvatar({ avatarUrl: uploaded.url })
    if (myProfile.value) {
      myProfile.value = { ...myProfile.value, avatarUrl: uploaded.url }
    } else {
      await loadProfile()
    }
    ElMessage.success('头像已更新')
  } catch (error) {
    ElMessage.error(getUploadErrorMessage(error, '头像上传失败'))
  } finally {
    uploadingAvatar.value = false
  }
}

async function handleLogout() {
  await authStore.logoutAll()
  router.push('/auth')
}

onMounted(loadProfile)
</script>

<style scoped>
.system-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 262px 1fr;
  background: linear-gradient(180deg, #edf4f4 0%, #f8fcfc 100%);
}

.side-nav {
  --text-primary: #f4fffd;
  --text-secondary: rgba(207, 243, 236, 0.74);
  --border-soft: rgba(134, 230, 213, 0.24);
  --hover-bg: rgba(240, 255, 252, 0.14);
  --active-bg: linear-gradient(135deg, rgba(18, 136, 117, 0.9), rgba(10, 114, 97, 0.94));

  background: radial-gradient(circle at 78% 10%, rgba(40, 171, 148, 0.38) 0, rgba(40, 171, 148, 0) 46%),
    linear-gradient(180deg, #072734 0%, #0b3f51 100%);
  color: var(--text-primary);
  padding: 16px 14px 14px;
  display: flex;
  flex-direction: column;
  box-shadow: inset -1px 0 0 rgba(255, 255, 255, 0.07);
}

.profile-card {
  border-radius: 16px;
  border: 1px solid var(--border-soft);
  background: rgba(255, 255, 255, 0.08);
  padding: 12px;
  display: grid;
  grid-template-columns: auto 1fr;
  column-gap: 10px;
  row-gap: 8px;
  align-items: center;
  backdrop-filter: blur(6px);
}

.profile-avatar {
  border: 2px solid rgba(172, 246, 233, 0.52);
}

.profile-meta {
  min-width: 0;
}

.profile-title {
  font-size: 17px;
  font-weight: 700;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.profile-subtitle {
  margin-top: 3px;
  font-size: 12px;
  color: var(--text-secondary);
}

.profile-upload {
  grid-column: 1 / -1;
}

.upload-btn {
  width: 100%;
  border-radius: 10px;
  border: 1px solid rgba(153, 242, 224, 0.3);
  background: rgba(255, 255, 255, 0.06);
  color: #defff8;
}

.upload-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
}

.nav-divider {
  height: 1px;
  margin: 14px 4px 10px;
  background: linear-gradient(90deg, rgba(134, 230, 213, 0), rgba(134, 230, 213, 0.58), rgba(134, 230, 213, 0));
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
  font-size: 16px;
  height: 46px;
  line-height: 46px;
  color: rgba(236, 255, 251, 0.94);
  border-radius: 12px;
  margin-bottom: 8px;
  transition: background-color 0.2s ease, transform 0.18s ease, box-shadow 0.2s ease, color 0.2s ease;
}

.menu :deep(.el-menu-item:hover) {
  background: var(--hover-bg);
  color: #f8fffe !important;
  transform: translateX(3px);
}

.menu :deep(.el-menu-item.is-active) {
  color: #ffffff;
  background: var(--active-bg);
  box-shadow: 0 8px 20px rgba(2, 29, 25, 0.42);
  transform: translateX(2px);
}

.menu-icon {
  margin-right: 10px;
  font-size: 18px;
  color: rgba(173, 249, 233, 0.8);
  transition: color 0.2s ease, transform 0.2s ease;
}

.menu :deep(.el-menu-item:hover) .menu-icon,
.menu :deep(.el-menu-item.is-active) .menu-icon {
  color: #ffffff;
  transform: scale(1.07);
}

.logout-btn {
  margin-top: 8px;
  width: 100%;
  justify-content: center;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(145, 236, 218, 0.26);
  color: #e9fff9;
  transition: background-color 0.18s ease, transform 0.18s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.18);
  transform: translateY(-1px);
}

.content {
  padding: 20px;
  animation: page-enter 0.2s ease;
}

@keyframes page-enter {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 980px) {
  .system-shell {
    grid-template-columns: 1fr;
  }

  .side-nav {
    min-height: auto;
  }
}
</style>
