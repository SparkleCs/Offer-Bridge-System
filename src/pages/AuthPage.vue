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
        <h2 class="title" style="font-size:28px;">验证码登录/注册</h2>
        <p class="desc">首次验证通过即注册</p>

        <el-form label-position="top" class="auth-form" @submit.prevent>
          <el-form-item label="选择角色">
            <el-radio-group v-model="selectedRole" class="role-group" size="large">
              <el-radio-button label="STUDENT">学生</el-radio-button>
              <el-radio-button label="AGENT">中介</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="手机号">
            <el-input v-model="phone" maxlength="11" size="large" placeholder="请输入手机号" />
          </el-form-item>

          <el-form-item label="短信验证码">
            <div class="code-row">
              <el-input v-model="code" maxlength="6" size="large" placeholder="请输入验证码" />
              <el-button class="code-btn" size="large" :disabled="countdown > 0 || !phone" @click="sendCode">
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <div class="agreement-row">
            <el-checkbox v-model="agreed">已阅读并同意《用户协议》《隐私政策》</el-checkbox>
          </div>

          <el-button class="submit-btn" type="primary" size="large" :loading="submitting" @click="submitSms">
            登录/注册
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

const selectedRole = ref<LoginRole>('STUDENT')
const phone = ref('')
const code = ref('')
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
      role: selectedRole.value === 'AGENT' ? 'AGENT_ORG' : 'STUDENT'
    } as const
    if (!payload.role) {
      ElMessage.error('登录角色缺失，请刷新后重试')
      return
    }
    if (import.meta.env.DEV) {
      console.info('[auth][loginBySms][payload]', payload)
    }
    const result = await authStore.loginBySms(payload)
    if (result.role === 'AGENT_ORG') {
      await router.push('/org-admin/verification')
    } else if (result.role === 'AGENT_MEMBER') {
      await router.push('/agent-workbench/communication')
    } else {
      await router.push('/')
    }
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
  padding: 18px 0;
}

.auth-shell {
  max-width: 1040px;
  margin: 0 auto;
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(135deg, #dff5fb 0%, #eef9ff 52%, #ffffff 100%);
  border: 1px solid #d3e6f6;
  box-shadow: 0 24px 46px rgba(27, 56, 92, 0.12);
  display: grid;
  grid-template-columns: 0.95fr 1.25fr;
}

.auth-aside {
  padding: 44px 34px;
  background: rgba(234, 247, 253, 0.78);
  border-right: 1px solid rgba(165, 202, 225, 0.45);
}

.brand {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 120px;
  height: 34px;
  border-radius: 999px;
  background: rgba(57, 167, 209, 0.16);
  color: #1f8ead;
  font-size: 13px;
  letter-spacing: 0.04em;
  font-weight: 700;
}

.auth-aside h3 {
  margin: 24px 0 10px;
  font-size: 28px;
  color: #2a405a;
}

.auth-aside > p {
  margin: 0;
  color: #5d738a;
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
  background: linear-gradient(135deg, #4eb9df, #2f8fd8);
}

.feature-title {
  font-weight: 700;
  color: #304861;
  margin-bottom: 4px;
}

.feature-desc {
  color: #6a8197;
  font-size: 14px;
}

.auth-panel {
  background: rgba(255, 255, 255, 0.94);
  padding: 52px 48px;
}

.title {
  margin: 0;
  text-align: center;
  font-size: 38px;
  color: #2b3d50;
}

.desc {
  margin: 10px 0 28px;
  text-align: center;
  color: #9aadb9;
  font-size: 15px;
}

.auth-form {
  max-width: 520px;
  margin: 0 auto;
}

.role-group {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  width: 100%;
}

.role-group :deep(.el-radio-button__inner) {
  width: 100%;
}

.code-row {
  display: grid;
  grid-template-columns: 1fr 150px;
  gap: 10px;
}

.code-btn {
  width: 100%;
  border-color: #b8d6f6;
}

.agreement-row {
  margin: 4px 0 18px;
  color: #6d7d8c;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border: none;
  background: linear-gradient(120deg, #31a7e7, #1678f3);
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
    font-size: 32px;
  }
}

@media (max-width: 640px) {
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
    font-size: 28px;
  }

  .code-row {
    grid-template-columns: 1fr 132px;
  }
}
</style>
