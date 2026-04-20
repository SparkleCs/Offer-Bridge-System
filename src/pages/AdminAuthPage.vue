<template>
  <section class="auth-wrap">
    <div class="auth-card">
      <h2>管理员登录</h2>
      <p class="sub">仅管理员账号可进入审核后台</p>
      <el-form label-position="top">
        <el-form-item label="手机号">
          <el-input v-model="phone" maxlength="11" placeholder="请输入管理员手机号" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="code-line">
            <el-input v-model="code" maxlength="6" placeholder="请输入验证码" />
            <el-button :loading="sending" @click="sendCode">发送验证码</el-button>
          </div>
        </el-form-item>
        <el-button class="submit-btn" type="primary" :loading="submitting" @click="submit">进入管理员端</el-button>
      </el-form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { useRouter } from 'vue-router'
import { adminSendSmsCode, adminSmsLogin } from '../services/auth'
import { ApiError } from '../services/http'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const phone = ref('')
const code = ref('')
const sending = ref(false)
const submitting = ref(false)

async function sendCode() {
  if (!/^1\d{10}$/.test(phone.value)) {
    ElMessage.warning('请输入正确手机号')
    return
  }
  sending.value = true
  try {
    const res = await adminSendSmsCode(phone.value)
    if (res?.mockCode) {
      code.value = res.mockCode
      ElNotification({ title: '开发验证码', message: `验证码：${res.mockCode}`, type: 'success', duration: 2000 })
    } else {
      ElNotification({ title: '发送成功', message: '验证码已发送', type: 'success', duration: 2000 })
    }
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '发送失败')
  } finally {
    sending.value = false
  }
}

async function submit() {
  if (!/^1\d{10}$/.test(phone.value) || !/^\d{4,6}$/.test(code.value)) {
    ElMessage.warning('请填写正确手机号和验证码')
    return
  }
  submitting.value = true
  try {
    const result = await adminSmsLogin({ phone: phone.value, code: code.value })
    authStore.applyAuth(result)
    await router.push('/admin/org-reviews')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '登录失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.auth-wrap {
  min-height: calc(100vh - 120px);
  display: grid;
  place-items: center;
}

.auth-card {
  width: min(460px, 94vw);
  padding: 24px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #dbe7f2;
  box-shadow: 0 14px 32px rgba(15, 34, 58, 0.08);
}

.auth-card h2 {
  margin: 0;
}

.sub {
  margin: 6px 0 16px;
  color: #6f8095;
}

.code-line {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}

.submit-btn {
  width: 100%;
}
</style>
