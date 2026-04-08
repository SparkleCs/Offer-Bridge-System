import { apiRequest } from './http'
import type {
  CompetitionView,
  ExchangeExperience,
  ResearchView,
  StudentProfile,
  StudentSourceCheckView,
  UpdateStudentAcademicPayload,
  UpdateStudentBasicPayload,
  VerificationStatus,
  VerificationSubmitPayload,
  WorkView
} from '../types/student'

export function getStudentProfile() {
  return apiRequest<StudentProfile>('/api/v1/student/profile', { method: 'GET' }, true)
}

export function updateStudentBasicProfile(payload: UpdateStudentBasicPayload) {
  return apiRequest<StudentProfile>(
    '/api/v1/student/profile/basic',
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function updateStudentAcademicProfile(payload: UpdateStudentAcademicPayload) {
  return apiRequest<StudentProfile>(
    '/api/v1/student/profile/academic',
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function getStudentResearch() {
  return apiRequest<ResearchView>('/api/v1/student/research', { method: 'GET' }, true)
}

export function saveStudentResearch(payload: ResearchView) {
  return apiRequest<ResearchView>(
    '/api/v1/student/research',
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function getStudentCompetition() {
  return apiRequest<CompetitionView>('/api/v1/student/competition', { method: 'GET' }, true)
}

export function saveStudentCompetition(payload: CompetitionView) {
  return apiRequest<CompetitionView>(
    '/api/v1/student/competition',
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function getStudentWork() {
  return apiRequest<WorkView>('/api/v1/student/work', { method: 'GET' }, true)
}

export function saveStudentWork(payload: WorkView) {
  return apiRequest<WorkView>(
    '/api/v1/student/work',
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function getStudentExchangeExperience() {
  return apiRequest<ExchangeExperience>('/api/v1/student/exchange', { method: 'GET' }, true)
}

export function saveStudentExchangeExperience(payload: ExchangeExperience) {
  return apiRequest<ExchangeExperience>(
    '/api/v1/student/exchange',
    {
      method: 'PUT',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function submitStudentVerification(payload: VerificationSubmitPayload) {
  return apiRequest<void>(
    '/api/v1/student/verification/submit',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function getStudentVerificationStatus() {
  return apiRequest<VerificationStatus>('/api/v1/student/verification/status', { method: 'GET' }, true)
}

export function uploadStudentFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return apiRequest<{ url: string }>(
    '/api/v1/files/upload',
    {
      method: 'POST',
      body: formData
    },
    true
  )
}

export function getStudentSourceCheck() {
  return apiRequest<StudentSourceCheckView>('/api/v1/student/debug/source-check', { method: 'GET' }, true)
}
