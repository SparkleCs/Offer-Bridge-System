<template>
  <section class="auth-stage fade-up">
    <div class="auth-shell">
      <aside class="auth-aside">
        <div class="brand">Offer Bridge</div>
        <h3>{{ sideContent.title }}</h3>
        <p>{{ sideContent.desc }}</p>

        <ul class="feature-list">
          <li v-for="item in sideContent.features" :key="item.title" class="feature-item">
            <span class="feature-dot"></span>
            <div>
              <div class="feature-title">{{ item.title }}</div>
              <div class="feature-desc">{{ item.desc }}</div>
            </div>
          </li>
        </ul>
      </aside>

      <section class="auth-panel">
        <button class="admin-entry" type="button" @click="goAdminAuth">平台管理员入口</button>
        <h2 class="title">{{ authMode === 'sms' ? '验证码登录/注册' : '密码登录' }}</h2>
        <p class="desc">{{ authMode === 'sms' ? '首次验证通过即注册' : '使用已设置的登录密码' }}</p>

        <el-form label-position="top" class="auth-form" @submit.prevent>
          <div class="mode-card">
            <el-radio-group v-model="authMode" class="mode-group" size="large">
              <el-radio-button label="sms">验证码登录</el-radio-button>
              <el-radio-button label="password">密码登录</el-radio-button>
            </el-radio-group>
          </div>

          <el-form-item label="选择角色" class="auth-field-item">
            <div class="field-card role-card">
              <el-radio-group v-model="selectedRole" class="role-group" size="large">
                <el-radio-button label="STUDENT">学生</el-radio-button>
                <el-radio-button label="AGENT">中介</el-radio-button>
              </el-radio-group>
            </div>
          </el-form-item>

          <el-form-item label="手机号" class="auth-field-item">
            <div class="field-card">
              <el-input v-model="phone" maxlength="11" size="large" placeholder="请输入手机号" />
            </div>
          </el-form-item>

          <template v-if="authMode === 'sms'">
            <el-form-item label="短信验证码" class="auth-field-item">
              <div class="field-card">
                <div class="code-row">
                  <el-input v-model="code" maxlength="6" size="large" placeholder="请输入验证码" />
                  <el-button class="code-btn" size="large" :disabled="countdown > 0 || !phone" @click="sendCode">
                    {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
                  </el-button>
                </div>
              </div>
            </el-form-item>

          </template>

          <el-form-item v-else label="密码" class="auth-field-item">
            <div class="field-card">
              <el-input
                v-model="password"
                maxlength="32"
                show-password
                size="large"
                placeholder="请输入密码"
                type="password"
              />
            </div>
          </el-form-item>

          <div class="agreement-row field-card agreement-card">
            <el-checkbox v-model="agreed">已阅读并同意《用户协议》《隐私政策》</el-checkbox>
          </div>

          <el-button class="submit-btn" type="primary" size="large" :loading="submitting" @click="submitSms">
            {{ authMode === 'sms' ? '登录/注册' : '密码登录' }}
          </el-button>
        </el-form>
      </section>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { useRouter } from 'vue-router'
import { ApiError } from '../services/http'
import { useAuthStore } from '../stores/auth'

type LoginRole = 'STUDENT' | 'AGENT'
type AuthMode = 'sms' | 'password'

const roleContents: Record<LoginRole, { title: string; desc: string; features: Array<{ title: string; desc: string }> }> = {
  STUDENT: {
    title: '留学更清晰',
    desc: '信息更清楚，选择更轻松。',
    features: [
      { title: '中介筛选更省心', desc: '多条件筛选，快速找到合适机构' },
      { title: '选校方案更合理', desc: '结合背景推荐更稳妥的院校组合' },
      { title: '申请进度看得见', desc: '每个节点清晰可见，不再反复追问' }
    ]
  },
  AGENT: {
    title: '获客更高效',
    desc: '对接更精准，转化更轻松。',
    features: [
      { title: '优质用户更集中', desc: '优先触达需求明确的申请人群' },
      { title: '匹配结果更直观', desc: '快速判断与自身服务的契合程度' },
      { title: '沟通转化更顺畅', desc: '过程统一管理，减少信息流失' }
    ]
  }
}

const router = useRouter()
const authStore = useAuthStore()

const authMode = ref<AuthMode>('sms')
const selectedRole = ref<LoginRole>('STUDENT')
const phone = ref('')
const code = ref('')
const password = ref('')
const agreed = ref(true)
const countdown = ref(0)
const timer = ref<number | null>(null)
const submitting = ref(false)

const sideContent = computed(() => roleContents[selectedRole.value])

function clearTimer() {
  if (timer.value) {
    window.clearInterval(timer.value)
    timer.value = null
  }
}

function goAdminAuth() {
  router.push('/admin-auth')
}

function currentRole() {
  return selectedRole.value === 'AGENT' ? 'AGENT_ORG' : 'STUDENT'
}

function isValidPassword(value: string) {
  return /^(?=.*[A-Za-z])(?=.*\d).{8,32}$/.test(value)
}

async function routeAfterLogin(role: string) {
  if (role === 'AGENT_ORG') {
    await router.push('/org-admin/verification')
  } else if (role === 'AGENT_MEMBER') {
    await router.push('/agent-workbench/communication')
  } else {
    await router.push('/')
  }
}

async function sendCode() {
  if (!/^1\d{10}$/.test(phone.value)) {
    ElMessage.warning('请输入有效手机号')
    return
  }

  try {
    const result = await authStore.sendCode({ phone: phone.value, scene: 'LOGIN_REGISTER' })
    if (import.meta.env.DEV) {
      console.info('[auth][sendCode]', {
        phone: phone.value,
        route: router.currentRoute.value.path,
        hasMockCode: Boolean(result?.mockCode)
      })
    }
    clearTimer()
    countdown.value = 60
    timer.value = window.setInterval(() => {
      countdown.value -= 1
      if (countdown.value <= 0) {
        clearTimer()
      }
    }, 1000)
    if (import.meta.env.DEV && result?.mockCode) {
      code.value = result.mockCode
    }
    if (result?.mockCode) {
      ElNotification({
        type: 'success',
        title: '验证码',
        message: `验证码：${result.mockCode}`,
        duration: 2000
      })
    } else {
      ElNotification({
        type: 'success',
        title: '提示',
        message: '验证码已发送',
        duration: 2000
      })
    }
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '发送失败')
  }
}

async function submitSms() {
  if (authMode.value === 'password') {
    await submitPasswordLogin()
    return
  }
  if (!/^1\d{10}$/.test(phone.value)) {
    ElMessage.warning('请输入有效手机号')
    return
  }
  if (!/^\d{4,6}$/.test(code.value)) {
    ElMessage.warning('请输入验证码')
    return
  }
  if (!agreed.value) {
    ElMessage.warning('请先阅读并同意协议')
    return
  }

  submitting.value = true
  try {
    const payload = {
      phone: phone.value,
      code: code.value,
      role: currentRole()
    } as const
    if (!payload.role) {
      ElMessage.error('登录角色缺失，请刷新后重试')
      return
    }
    if (import.meta.env.DEV) {
      console.info('[auth][loginBySms][payload]', payload)
    }
    const result = await authStore.loginBySms(payload)
    await routeAfterLogin(result.role)
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '登录失败')
  } finally {
    submitting.value = false
  }
}

async function submitPasswordLogin() {
  if (!/^1\d{10}$/.test(phone.value)) {
    ElMessage.warning('请输入有效手机号')
    return
  }
  if (!isValidPassword(password.value)) {
    ElMessage.warning('密码需为8-32位且包含字母和数字')
    return
  }
  if (!agreed.value) {
    ElMessage.warning('请先阅读并同意协议')
    return
  }

  submitting.value = true
  try {
    const result = await authStore.loginByPassword({
      phone: phone.value,
      password: password.value,
      role: currentRole()
    })
    await routeAfterLogin(result.role)
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '登录失败')
  } finally {
    submitting.value = false
  }
}

onBeforeUnmount(() => {
  clearTimer()
})
</script>

<style scoped>
.auth-stage {
  --bg-soft: #f4f8fd;
  --panel-stroke: #d4e3f2;
  --panel-shadow: 0 20px 44px rgba(33, 74, 122, 0.12);
  --card-bg: #f8fbff;
  --card-stroke: #d8e5f3;
  --focus-stroke: #77aee8;
  --focus-shadow: 0 10px 18px rgba(52, 128, 201, 0.2);
  --text-main: #2a3f56;
  --text-sub: #6a7f92;
  min-height: calc(100vh - 40px);
  padding: 20px;
  background: linear-gradient(145deg, #f3f8fd, #eaf2fa);
}

.auth-shell {
  max-width: 1040px;
  margin: 0 auto;
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(136deg, #eff7fd 0%, #f8fcff 45%, #ffffff 100%);
  border: 1px solid var(--panel-stroke);
  box-shadow: var(--panel-shadow);
  display: grid;
  grid-template-columns: 0.95fr 1.25fr;
}

.auth-aside {
  padding: 44px 34px;
  background: rgba(236, 246, 252, 0.85);
  border-right: 1px solid rgba(166, 198, 223, 0.48);
}

.brand {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 120px;
  height: 34px;
  border-radius: 999px;
  background: rgba(63, 164, 217, 0.14);
  color: #237fa8;
  border: 1px solid rgba(133, 194, 228, 0.35);
  font-size: 13px;
  letter-spacing: 0.04em;
  font-weight: 700;
}

.auth-aside h3 {
  margin: 24px 0 10px;
  font-size: 28px;
  color: #2a415a;
}

.auth-aside > p {
  margin: 0;
  color: #607b91;
  line-height: 1.7;
}

.feature-list {
  margin: 30px 0 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 18px;
}

.feature-item {
  display: grid;
  grid-template-columns: 12px 1fr;
  gap: 12px;
  align-items: start;
}

.feature-dot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
  margin-top: 5px;
  background: linear-gradient(135deg, #64c7e8, #4b96de);
}

.feature-title {
  font-weight: 700;
  color: #314c66;
  margin-bottom: 4px;
}

.feature-desc {
  color: #6b8398;
  font-size: 14px;
}

.auth-panel {
  position: relative;
  background: rgba(255, 255, 255, 0.94);
  padding: 52px 48px;
}

.admin-entry {
  position: absolute;
  top: 14px;
  right: 16px;
  border: none;
  background: transparent;
  color: #6f89a4;
  font-size: 12px;
  cursor: pointer;
  padding: 4px 6px;
  border-radius: 8px;
  transition: color 0.18s ease, background-color 0.18s ease;
}

.admin-entry:hover {
  color: #436c97;
  background: rgba(115, 144, 173, 0.12);
}

.title {
  margin: 0;
  text-align: center;
  font-size: 32px;
  color: #2a3f56;
  letter-spacing: 0.02em;
}

.desc {
  margin: 10px 0 28px;
  text-align: center;
  color: #7f94a6;
  font-size: 15px;
}

.auth-form {
  max-width: 520px;
  margin: 0 auto;
}

.mode-card {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid var(--card-stroke);
  border-radius: 14px;
  background: #f5f9fd;
  padding: 8px;
  margin-bottom: 16px;
}

.mode-group {
  display: flex;
  width: 100%;
}

.mode-group :deep(.el-radio-button) {
  flex: 1 1 50%;
}

.mode-group :deep(.el-radio-button__inner) {
  width: 100%;
  border: none;
  border-radius: 10px;
  background: transparent;
}

.auth-field-item {
  margin-bottom: 14px;
}

.auth-field-item :deep(.el-form-item__label) {
  color: var(--text-main);
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 8px;
  transition: color 0.2s ease;
}

.auth-field-item:focus-within :deep(.el-form-item__label) {
  color: #245f95;
}

.field-card {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid var(--card-stroke);
  border-radius: 14px;
  background: var(--card-bg);
  padding: 8px;
  transition: border-color 0.18s ease-out, box-shadow 0.2s ease-out, transform 0.18s ease-out, background-color 0.2s ease-out;
}

.field-card:hover {
  border-color: #c4d7ea;
}

.field-card:focus-within {
  border-color: var(--focus-stroke);
  box-shadow: var(--focus-shadow);
  transform: translateY(-1px);
  background: #ffffff;
}

.field-card :deep(.el-input__wrapper) {
  box-shadow: none !important;
  background: transparent;
  border-radius: 10px;
}

.field-card :deep(.el-input__inner) {
  font-size: 16px;
  color: #2b4259;
}

.role-group {
  display: flex;
  width: 100%;
}

.role-group :deep(.el-radio-button) {
  flex: 1 1 50%;
}

.role-group :deep(.el-radio-button__inner) {
  width: 100%;
  border-radius: 10px;
  border: none;
  background: #f3f7fc;
  transition: all 0.2s ease-out;
}

.role-group :deep(.el-radio-button__inner:hover) {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(66, 130, 191, 0.12);
}

.role-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  box-shadow: 0 10px 18px rgba(57, 131, 207, 0.26);
}

.code-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 150px;
  gap: 10px;
  width: 100%;
}

.code-btn {
  width: 100%;
  border-color: #b8d6f6;
  border-radius: 10px;
  transition: transform 0.18s ease-out, box-shadow 0.18s ease-out, background-color 0.18s ease-out;
}

.code-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(63, 136, 201, 0.16);
}

.code-btn:active {
  transform: translateY(0);
}

.code-btn.is-disabled {
  opacity: 0.72;
}

.agreement-row {
  margin: 4px 0 18px;
  color: #6d7d8c;
}

.agreement-card {
  padding: 10px 12px;
  border-radius: 12px;
}

.agreement-card:focus-within {
  transform: translateY(0);
}

.submit-btn {
  width: 100%;
  height: 48px;
  border: none;
  background: linear-gradient(120deg, #36a6ef, #2f78de);
  box-shadow: 0 12px 22px rgba(43, 113, 193, 0.3);
  transition: transform 0.2s ease-out, box-shadow 0.2s ease-out, filter 0.2s ease-out;
}

.submit-btn:hover {
  transform: translateY(-1px);
  filter: brightness(1.04);
  box-shadow: 0 18px 30px rgba(26, 122, 218, 0.4);
}

@media (max-width: 980px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }

  .auth-aside {
    border-right: none;
    border-bottom: 1px solid rgba(165, 202, 225, 0.45);
  }

  .auth-panel {
    padding: 34px 24px;
  }

  .title {
    font-size: 30px;
  }
}

@media (max-width: 640px) {
  .auth-stage {
    padding: 10px;
  }

  .auth-aside {
    padding: 30px 20px;
  }

  .auth-aside h3 {
    font-size: 24px;
  }

  .auth-panel {
    padding: 30px 18px;
  }

  .title {
    font-size: 26px;
  }

  .code-row {
    grid-template-columns: 1fr 132px;
  }

  .field-card {
    padding: 6px;
  }
}
</style>
