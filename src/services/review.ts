import { apiRequest } from './http'
import type {
  AgencyDashboard,
  MemberReviewItem,
  OrderReviewStatus,
  RatingSummary,
  SubmitOrderReviewPayload,
  TeamMemberReviewSummary
} from '../types/review'

export function getOrderReviewStatus(orderId: number) {
  return apiRequest<OrderReviewStatus>(`/api/v1/reviews/orders/${orderId}/status`, { method: 'GET' }, true)
}

export function submitOrderReview(orderId: number, payload: SubmitOrderReviewPayload) {
  return apiRequest<OrderReviewStatus>(`/api/v1/reviews/orders/${orderId}`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function getTeamRatingSummaries(teamIds: number[]) {
  const ids = Array.from(new Set(teamIds.filter(Boolean)))
  if (ids.length === 0) return Promise.resolve([] as RatingSummary[])
  return apiRequest<RatingSummary[]>(`/api/v1/reviews/discovery/teams/summaries?teamIds=${ids.join(',')}`, { method: 'GET' }, false)
}

export function getTeamRatingSummary(teamId: number) {
  return apiRequest<RatingSummary>(`/api/v1/reviews/discovery/teams/${teamId}/summary`, { method: 'GET' }, false)
}

export function getTeamMemberReviewSummaries(teamId: number) {
  return apiRequest<TeamMemberReviewSummary[]>(`/api/v1/reviews/discovery/teams/${teamId}/members`, { method: 'GET' }, false)
}

export function getTeamMemberReviews(teamId: number, memberId: number) {
  return apiRequest<MemberReviewItem[]>(`/api/v1/reviews/discovery/teams/${teamId}/members/${memberId}/reviews`, { method: 'GET' }, false)
}

export function getAgencyReviewDashboard() {
  return apiRequest<AgencyDashboard>('/api/v1/reviews/agency/dashboard', { method: 'GET' }, true)
}
