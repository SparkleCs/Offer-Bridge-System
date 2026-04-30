import { apiRequest } from './http'
import type {
  AgencyTeamStudentRecommendationPage,
  StudentAgencyTeamRecommendationItem
} from '../types/recommendation'

export function listStudentAgencyTeamRecommendations() {
  return apiRequest<StudentAgencyTeamRecommendationItem[]>('/api/v1/recommendations/student/agency-teams', { method: 'GET' }, true)
}

export function listAgencyTeamStudentRecommendations(params: {
  teamId: number
  page?: number
  pageSize?: number
}) {
  const search = new URLSearchParams()
  search.append('teamId', String(params.teamId))
  if (params.page) search.append('page', String(params.page))
  if (params.pageSize) search.append('pageSize', String(params.pageSize))
  return apiRequest<AgencyTeamStudentRecommendationPage>(`/api/v1/recommendations/agency/team-students?${search.toString()}`, { method: 'GET' }, true)
}
