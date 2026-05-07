import { apiRequest, apiRequestOptionalAuth } from './http'
import type {
  ApplicationListView,
  ProgramDetail,
  ProgramListItem,
  SchoolDetail,
  SchoolListItem,
  UniversityMeta
} from '../types/university'

interface SchoolFilterParams {
  countryCode?: string
  subjectCategoryCode?: string
  directionCode?: string
  rankMin?: number
  rankMax?: number
  rankingSource?: 'QS' | 'USNEWS'
  keyword?: string
}

interface ProgramFilterParams {
  schoolId?: number
  countryCode?: string
  subjectCategoryCode?: string
  directionCode?: string
  keyword?: string
}

function toQuery(params: object) {
  const searchParams = new URLSearchParams()
  for (const [key, value] of Object.entries(params as Record<string, string | number | undefined>)) {
    if (value !== undefined && value !== null && String(value) !== '') {
      searchParams.set(key, String(value))
    }
  }
  const text = searchParams.toString()
  return text ? `?${text}` : ''
}

export function getUniversityMeta() {
  return apiRequestOptionalAuth<UniversityMeta>('/api/v1/universities/meta', { method: 'GET' })
}

export function listSchools(params: SchoolFilterParams) {
  return apiRequestOptionalAuth<SchoolListItem[]>(`/api/v1/universities/schools${toQuery(params)}`, { method: 'GET' })
}

export function getSchoolDetail(schoolId: number) {
  return apiRequestOptionalAuth<SchoolDetail>(`/api/v1/universities/schools/${schoolId}`, { method: 'GET' })
}

export function listPrograms(params: ProgramFilterParams) {
  return apiRequestOptionalAuth<ProgramListItem[]>(`/api/v1/universities/programs${toQuery(params)}`, { method: 'GET' })
}

export function getProgramDetail(programId: number) {
  return apiRequestOptionalAuth<ProgramDetail>(`/api/v1/universities/programs/${programId}`, { method: 'GET' })
}

export function getApplicationList() {
  return apiRequest<ApplicationListView>('/api/v1/student/application-list', { method: 'GET' }, true)
}

export function addApplication(programId: number, noteText = '') {
  return apiRequest<ApplicationListView>(
    '/api/v1/student/application-list',
    {
      method: 'POST',
      body: JSON.stringify({ programId, noteText })
    },
    true
  )
}

export function updateApplicationStatus(applicationId: number, statusCode: string) {
  return apiRequest<ApplicationListView>(
    `/api/v1/student/application-list/${applicationId}/status`,
    {
      method: 'PUT',
      body: JSON.stringify({ statusCode })
    },
    true
  )
}

export function removeApplication(applicationId: number) {
  return apiRequest<ApplicationListView>(`/api/v1/student/application-list/${applicationId}`, { method: 'DELETE' }, true)
}
