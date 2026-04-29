<template>
  <section class="account-security-panel">
    <div class="security-head">
      <div>
        <h3>账号安全</h3>
        <p>{{ hasPassword ? '修改登录密码' : '首次设置登录密码' }}</p>
      </div>
    </div>

    <el-form label-position="top" class="security-form" @submit.prevent>
      <el-row :gutter="12">
        <el-col v-if="hasPassword" :span="12">
          <el-form-item label="当前密码">
            <el-input
              v-model="currentPassword"
              maxlength="32"
              show-password
              placeholder="请输入当前密码"
              type="password"
            />
          </el-form-item>
        </el-col>
        <el-col :span="hasPassword ? 12 : 24">
          <el-form-item label="新密码">
            <el-input
              v-model="newPassword"
              maxlength="32"
              show-password
              placeholder="8-32位，包含字母和数字"
              type="password"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-button type="primary" :loading="saving || loadingStatus" @click="submit">
        {{ hasPassword ? '修改密码' : '设置密码' }}
      </el-button>
    </el-form>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPasswordStatus, updatePassword } from '../services/auth'
import { ApiError } from '../services/http'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const currentPassword = ref('')
const newPassword = ref('')
const saving = ref(false)
const hasPassword = ref(Boolean(authStore.authMeta?.hasPassword))
const loadingStatus = ref(false)

function isValidPassword(value: string) {
  return /^(?=.*[A-Za-z])(?=.*\d).{8,32}$/.test(value)
}

async function submit() {
  if (hasPassword.value && !currentPassword.value) {
    ElMessage.warning('请输入当前密码')
    return
  }
  if (!isValidPassword(newPassword.value)) {
    ElMessage.warning('密码需为8-32位且包含字母和数字')
    return
  }

  saving.value = true
  try {
    await updatePassword({
      currentPassword: hasPassword.value ? currentPassword.value : undefined,
      newPassword: newPassword.value
    })
    currentPassword.value = ''
    newPassword.value = ''
    hasPassword.value = true
    authStore.setPasswordConfigured(true)
    ElMessage.success('密码已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  loadingStatus.value = true
  try {
    const status = await getPasswordStatus()
    hasPassword.value = status.hasPassword
    authStore.setPasswordConfigured(status.hasPassword)
  } catch {
    ElMessage.warning('暂时无法确认密码状态，请稍后重试')
  } finally {
    loadingStatus.value = false
  }
})
</script>

<style scoped>
.account-security-panel {
  border: 1px solid rgba(192, 211, 231, 0.9);
  border-radius: 14px;
  padding: 16px;
  background: #fff;
}

.security-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.security-head h3 {
  margin: 0;
  color: #2a3f56;
  font-size: 18px;
}

.security-head p {
  margin: 4px 0 0;
  color: #76879a;
  font-size: 13px;
}

.security-form :deep(.el-form-item) {
  margin-bottom: 12px;
}

@media (max-width: 720px) {
  .security-form :deep(.el-col) {
    max-width: 100%;
    flex: 0 0 100%;
  }
}
</style>
