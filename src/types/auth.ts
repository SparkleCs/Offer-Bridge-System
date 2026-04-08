export type UserRole = 'STUDENT' | 'AGENT_INDIVIDUAL' | 'AGENT_ORG' | 'ADMIN'

export interface ApiResponse<T> {
  code: string
  message: string
  data: T
  traceId?: string | null
  detail?: string | null
}

export interface AuthResult {
  accessToken: string
  refreshToken: string
  userId: number
  role: UserRole
  profileCompleted: boolean
  verificationCompleted: boolean
}

export interface SendSmsPayload {
  phone: string
  scene: 'LOGIN_REGISTER'
}

export interface SmsLoginPayload {
  phone: string
  code: string
  role: 'STUDENT' | 'AGENT_ORG'
}

export interface RefreshPayload {
  refreshToken: string
}
