<template>
  <div class="system-shell org-shell">
    <aside class="side-nav">
      <div class="profile-card">
        <el-avatar :size="54" :src="orgProfile?.logoUrl || undefined" class="profile-avatar">{{ orgInitial }}</el-avatar>
        <div class="profile-meta">
          <div class="profile-title">{{ orgName }}</div>
          <div class="profile-subtitle">Organization Console</div>
        </div>
        <el-upload :show-file-list="false" :http-request="onUploadLogo" accept="image/*" class="profile-upload">
          <el-button size="small" :loading="uploadingLogo" class="upload-btn">更换Logo</el-button>
        </el-upload>
      </div>

      <div class="nav-divider"></div>

      <el-menu :default-active="route.path" class="menu" @select="go">
        <el-menu-item index="/org-admin/verification">
          <el-icon class="menu-icon"><Checked /></el-icon>
          <span>公司认证</span>
        </el-menu-item>
        <el-menu-item index="/org-admin/members">
          <el-icon class="menu-icon"><UserFilled /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/org-admin/permissions">
          <el-icon class="menu-icon"><Key /></el-icon>
          <span>权限管理</span>
        </el-menu-item>
        <el-menu-item index="/org-admin/company">
          <el-icon class="menu-icon"><OfficeBuilding /></el-icon>
          <span>公司信息</span>
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
import { Checked, Key, OfficeBuilding, UserFilled } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getAgencyOrgProfile, updateAgencyOrgProfile, uploadFile } from '../services/agency'
import type { AgencyOrgProfile } from '../types/agency'
import { getUploadErrorMessage, validateUploadFileSize } from '../utils/upload'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const orgProfile = ref<AgencyOrgProfile | null>(null)
const uploadingLogo = ref(false)

const orgName = computed(() => orgProfile.value?.brandName || orgProfile.value?.orgName || '机构管理端')
const orgInitial = computed(() => (orgName.value?.slice(0, 1) || '机').toUpperCase())

function go(path: string) {
  if (path !== route.path) {
    router.push(path)
  }
}

async function loadOrgProfile() {
  try {
    orgProfile.value = await getAgencyOrgProfile()
  } catch {
    orgProfile.value = null
  }
}

async function onUploadLogo(options: UploadRequestOptions) {
  const file = options.file as File
  if (!validateUploadFileSize(file)) {
    return
  }
  if (!orgProfile.value?.id) {
    ElMessage.warning('请先在公司信息页完善机构档案，再上传Logo')
    return
  }
  uploadingLogo.value = true
  try {
    const uploaded = await uploadFile(file, 'avatar')
    const updated = await updateAgencyOrgProfile({ ...orgProfile.value, logoUrl: uploaded.url })
    orgProfile.value = updated
    ElMessage.success('Logo已更新')
  } catch (error) {
    ElMessage.error(getUploadErrorMessage(error, 'Logo上传失败'))
  } finally {
    uploadingLogo.value = false
  }
}

async function handleLogout() {
  await authStore.logoutAll()
  router.push('/auth')
}

onMounted(loadOrgProfile)
</script>

<style scoped>
.system-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 262px 1fr;
  background: linear-gradient(180deg, #ecf2fa 0%, #f7faff 100%);
}

.side-nav {
  --text-primary: #f5fbff;
  --text-secondary: rgba(217, 237, 255, 0.74);
  --border-soft: rgba(149, 199, 249, 0.18);
  --hover-bg: rgba(255, 255, 255, 0.14);
  --active-bg: linear-gradient(135deg, rgba(26, 112, 191, 0.92), rgba(11, 84, 158, 0.92));

  background: radial-gradient(circle at 15% 8%, rgba(66, 141, 222, 0.46) 0, rgba(66, 141, 222, 0) 44%),
    linear-gradient(180deg, #0e223b 0%, #103250 100%);
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
  border: 2px solid rgba(190, 226, 255, 0.5);
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
  border: 1px solid rgba(173, 217, 255, 0.32);
  background: rgba(255, 255, 255, 0.06);
  color: #e8f4ff;
}

.upload-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

.nav-divider {
  height: 1px;
  margin: 14px 4px 10px;
  background: linear-gradient(90deg, rgba(149, 199, 249, 0), rgba(149, 199, 249, 0.55), rgba(149, 199, 249, 0));
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
  color: rgba(241, 249, 255, 0.9);
  border-radius: 12px;
  margin-bottom: 8px;
  transition: background-color 0.2s ease, transform 0.18s ease, box-shadow 0.2s ease, color 0.2s ease;
}

.menu :deep(.el-menu-item:hover) {
  background: var(--hover-bg);
  color: #ffffff !important;
  transform: translateX(3px);
}

.menu :deep(.el-menu-item.is-active) {
  color: #ffffff;
  background: var(--active-bg);
  box-shadow: 0 8px 20px rgba(2, 22, 45, 0.44);
  transform: translateX(2px);
}

.menu-icon {
  margin-right: 10px;
  font-size: 18px;
  color: rgba(192, 231, 255, 0.7);
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
  background: rgba(255, 255, 255, 0.09);
  border: 1px solid rgba(174, 220, 255, 0.24);
  color: #e9f5ff;
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
