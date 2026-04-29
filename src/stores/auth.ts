import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { logout, passwordLogin, sendSmsCode, smsLoginOrRegister } from '../services/auth'
import { authStorageKeys, clearLocalAuth, refreshByToken } from '../services/http'
import { getStudentProfile } from '../services/student'
import type { AuthResult, PasswordLoginPayload, SendSmsPayload, SmsLoginPayload } from '../types/auth'
import type { StudentProfile } from '../types/student'

const { accessTokenKey, refreshTokenKey, authMetaKey } = authStorageKeys()

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
