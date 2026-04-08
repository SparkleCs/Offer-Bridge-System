import { apiRequest } from './http'
import type {
  AuthResult,
  RefreshPayload,
  SendSmsPayload,
  SmsLoginPayload
} from '../types/auth'

export function sendSmsCode(payload: SendSmsPayload) {
  return apiRequest<void>(
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
