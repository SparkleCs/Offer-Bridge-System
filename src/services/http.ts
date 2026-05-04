import type { ApiResponse, AuthResult } from '../types/auth'

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export class ApiError extends Error {
  code: string
  status: number
  traceId: string | null
  detail: string | null

  constructor(message: string, code: string, status = 400, traceId: string | null = null, detail: string | null = null) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.status = status
    this.traceId = traceId
    this.detail = detail
  }
}

const ACCESS_TOKEN_KEY = 'study_portal_access_token'
const REFRESH_TOKEN_KEY = 'study_portal_refresh_token'
const AUTH_META_KEY = 'study_portal_auth_meta'
const TOKEN_REFRESH_SKEW_SECONDS = 60

let refreshPromise: Promise<AuthResult> | null = null

export function readAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY)
}

function readRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

function decodeJwtPayload(token: string) {
  try {
    const payload = token.split('.')[1]
    if (!payload) return null
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/')
    const padded = normalized.padEnd(normalized.length + (4 - normalized.length % 4) % 4, '=')
    return JSON.parse(atob(padded)) as { exp?: number }
  } catch {
    return null
  }
}

export function isAccessTokenExpiring(token: string | null, skewSeconds = TOKEN_REFRESH_SKEW_SECONDS) {
  if (!token) return true
  const payload = decodeJwtPayload(token)
  if (!payload?.exp) return true
  return payload.exp * 1000 <= Date.now() + skewSeconds * 1000
}

function persistAuth(result: AuthResult) {
  localStorage.setItem(ACCESS_TOKEN_KEY, result.accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, result.refreshToken)
  localStorage.setItem(
    AUTH_META_KEY,
    JSON.stringify({
      userId: result.userId,
      role: result.role,
      profileCompleted: result.profileCompleted,
      verificationCompleted: result.verificationCompleted,
      hasPassword: result.hasPassword
    })
  )
}

async function doRequest<T>(
  path: string,
  init: RequestInit = {},
  withAuth = false,
  retry = true
): Promise<T> {
  const headers = new Headers(init.headers || {})
  if (!(init.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  if (withAuth) {
    const token = await ensureAccessToken()
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers
  })

  const body = (await response.json()) as ApiResponse<T>
  if (body.code === 'OK') {
    return body.data
  }

  if (body.code === 'BIZ_UNAUTHORIZED' && withAuth && retry) {
    const refreshToken = readRefreshToken()
    if (refreshToken) {
      try {
        await refreshByToken(refreshToken)
        return doRequest<T>(path, init, withAuth, false)
      } catch {
        clearLocalAuth()
      }
    }
  }

  throw new ApiError(
    body.message || '请求失败',
    body.code || 'BIZ_REQUEST_ERROR',
    response.status,
    body.traceId || null,
    body.detail || null
  )
}

export async function refreshByToken(refreshToken: string): Promise<AuthResult> {
  const result = await doRequest<AuthResult>(
    '/api/v1/auth/refresh',
    {
      method: 'POST',
      body: JSON.stringify({ refreshToken })
    },
    false,
    false
  )

  persistAuth(result)
  return result
}

export async function ensureAccessToken() {
  const accessToken = readAccessToken()
  if (accessToken && !isAccessTokenExpiring(accessToken)) {
    return accessToken
  }

  const refreshToken = readRefreshToken()
  if (!refreshToken) {
    clearLocalAuth()
    throw new ApiError('未登录或登录已过期', 'BIZ_UNAUTHORIZED', 401)
  }

  try {
    if (!refreshPromise) {
      refreshPromise = refreshByToken(refreshToken).finally(() => {
        refreshPromise = null
      })
    }
    const result = await refreshPromise
    return result.accessToken
  } catch {
    clearLocalAuth()
    throw new ApiError('未登录或登录已过期', 'BIZ_UNAUTHORIZED', 401)
  }
}

export async function apiRequest<T>(path: string, init: RequestInit = {}, withAuth = false) {
  return doRequest<T>(path, init, withAuth, true)
}

export async function apiRequestOptionalAuth<T>(path: string, init: RequestInit = {}) {
  const token = readAccessToken()
  const headers = new Headers(init.headers || {})
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }
  return doRequest<T>(path, { ...init, headers }, false, false)
}

export function clearLocalAuth() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  localStorage.removeItem(AUTH_META_KEY)
}

export function authStorageKeys() {
  return {
    accessTokenKey: ACCESS_TOKEN_KEY,
    refreshTokenKey: REFRESH_TOKEN_KEY,
    authMetaKey: AUTH_META_KEY
  }
}
