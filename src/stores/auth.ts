import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { logout, passwordLogin, sendSmsCode, smsLoginOrRegister } from '../services/auth'
import { authStorageKeys, clearLocalAuth, refreshByToken } from '../services/http'
import { getStudentProfile } from '../services/student'
import type { AuthResult, PasswordLoginPayload, SendSmsPayload, SmsLoginPayload } from '../types/auth'
import type { StudentProfile } from '../types/student'

const { accessTokenKey, refreshTokenKey, authMetaKey } = authStorageKeys()

// 学习入口：Pinia 里的 auth store 是前端登录态中心。
// token 负责接口鉴权，authMeta 负责前端角色跳转和资料完成状态展示。
function readAuthMeta() {
  try {
    return JSON.parse(localStorage.getItem(authMetaKey) || 'null') as {
      userId: number
      role: string
      profileCompleted: boolean
      verificationCompleted: boolean
      hasPassword?: boolean
    } | null
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(localStorage.getItem(accessTokenKey))
  const refreshToken = ref<string | null>(localStorage.getItem(refreshTokenKey))
  const authMeta = ref(readAuthMeta())
  const profile = ref<StudentProfile | null>(null)

  const isLoggedIn = computed(() => Boolean(accessToken.value && refreshToken.value))
  const profileCompleted = computed(() => authMeta.value?.profileCompleted ?? false)
  const verificationCompleted = computed(() => authMeta.value?.verificationCompleted ?? false)

  function applyAuth(result: AuthResult) {
    // 登录/刷新成功后同时更新内存状态和 localStorage，刷新页面后仍能恢复登录态。
    accessToken.value = result.accessToken
    refreshToken.value = result.refreshToken
    authMeta.value = {
      userId: result.userId,
      role: result.role,
      profileCompleted: result.profileCompleted,
      verificationCompleted: result.verificationCompleted,
      hasPassword: result.hasPassword
    }
    localStorage.setItem(accessTokenKey, result.accessToken)
    localStorage.setItem(refreshTokenKey, result.refreshToken)
    localStorage.setItem(authMetaKey, JSON.stringify(authMeta.value))
  }

  async function loginBySms(payload: SmsLoginPayload) {
    const result = await smsLoginOrRegister(payload)
    applyAuth(result)
    if (result.role === 'STUDENT') {
      await loadProfile().catch(() => null)
    }
    return result
  }

  async function loginByPassword(payload: PasswordLoginPayload) {
    const result = await passwordLogin(payload)
    applyAuth(result)
    if (result.role === 'STUDENT') {
      await loadProfile().catch(() => null)
    }
    return result
  }

  function sendCode(payload: SendSmsPayload) {
    return sendSmsCode(payload)
  }

  function setPasswordConfigured(value: boolean) {
    if (!authMeta.value) return
    authMeta.value.hasPassword = value
    localStorage.setItem(authMetaKey, JSON.stringify(authMeta.value))
  }

  async function refreshSession() {
    // 路由守卫会调用这里恢复会话；失败时清理本地状态，让用户回到登录页。
    if (!refreshToken.value) return false
    try {
      const result = await refreshByToken(refreshToken.value)
      applyAuth(result)
      return true
    } catch {
      doLogout()
      return false
    }
  }

  async function loadProfile() {
    // 学生画像是后续 AI 推荐、机构搜索、审核状态展示的基础数据。
    if (!isLoggedIn.value) return null
    const data = await getStudentProfile()
    profile.value = data
    if (authMeta.value) {
      authMeta.value.profileCompleted = data.profileCompleted
      localStorage.setItem(authMetaKey, JSON.stringify(authMeta.value))
    }
    return data
  }

  async function doServerLogout() {
    if (!refreshToken.value) return
    await logout({ refreshToken: refreshToken.value }).catch(() => null)
  }

  function doLogout() {
    accessToken.value = null
    refreshToken.value = null
    authMeta.value = null
    profile.value = null
    clearLocalAuth()
  }

  async function logoutAll() {
    await doServerLogout()
    doLogout()
  }

  return {
    accessToken,
    refreshToken,
    authMeta,
    profile,
    isLoggedIn,
    profileCompleted,
    verificationCompleted,
    applyAuth,
    loginBySms,
    loginByPassword,
    sendCode,
    setPasswordConfigured,
    refreshSession,
    loadProfile,
    doLogout,
    logoutAll
  }
})
