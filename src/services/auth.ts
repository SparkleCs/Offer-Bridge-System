import { apiRequest } from './http'
import type {
  AuthResult,
  PasswordLoginPayload,
  PasswordStatusResult,
  RefreshPayload,
  AdminSmsLoginPayload,
  SendSmsPayload,
  SendSmsResult,
  UpdatePasswordPayload,
  SmsLoginPayload
} from '../types/auth'

export function sendSmsCode(payload: SendSmsPayload) {
  return apiRequest<SendSmsResult>(
    '/api/v1/auth/sms/send',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    false
  )
}

export function smsLoginOrRegister(payload: SmsLoginPayload) {
  return apiRequest<AuthResult>(
    '/api/v1/auth/sms/login-or-register',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    false
  )
}

export function passwordLogin(payload: PasswordLoginPayload) {
  return apiRequest<AuthResult>(
    '/api/v1/auth/password/login',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    false
  )
}

export function updatePassword(payload: UpdatePasswordPayload) {
  return apiRequest<void>(
    '/api/v1/auth/password/update',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function getPasswordStatus() {
  return apiRequest<PasswordStatusResult>('/api/v1/auth/password/status', { method: 'GET' }, true)
}

export function adminSendSmsCode(phone: string) {
  return apiRequest<SendSmsResult>(
    '/api/v1/auth/admin/sms/send',
    {
      method: 'POST',
      body: JSON.stringify({ phone, scene: 'ADMIN_LOGIN' })
    },
    false
  )
}

export function adminSmsLogin(payload: AdminSmsLoginPayload) {
  return apiRequest<AuthResult>(
    '/api/v1/auth/admin/sms/login',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    false
  )
}

export function refreshSession(payload: RefreshPayload) {
  return apiRequest<AuthResult>(
    '/api/v1/auth/refresh',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    false
  )
}

export function logout(payload: RefreshPayload) {
  return apiRequest<void>(
    '/api/v1/auth/logout',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    true
  )
}
