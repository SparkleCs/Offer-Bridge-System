import { apiRequest } from './http'
import type { AdminPagedResult, AdminReviewDetail, AdminReviewListItem, ReviewSubjectType } from '../types/admin'
import type { SystemNotificationItem, SystemNotificationPagedResult } from '../types/message'

function buildQuery(params: Record<string, string | number | undefined>) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') search.append(k, String(v))
  })
  const query = search.toString()
  return query ? `?${query}` : ''
}

export function listAdminOrgReviews(params: { page?: number; pageSize?: number; status?: string; keyword?: string } = {}) {
  return apiRequest<AdminPagedResult<AdminReviewListItem>>(`/api/v1/admin/reviews/orgs${buildQuery(params)}`, { method: 'GET' }, true)
}

export function listAdminMemberReviews(params: { page?: number; pageSize?: number; status?: string; keyword?: string } = {}) {
  return apiRequest<AdminPagedResult<AdminReviewListItem>>(`/api/v1/admin/reviews/members${buildQuery(params)}`, { method: 'GET' }, true)
}

export function listAdminStudentReviews(params: { page?: number; pageSize?: number; status?: string; keyword?: string } = {}) {
  return apiRequest<AdminPagedResult<AdminReviewListItem>>(`/api/v1/admin/reviews/students${buildQuery(params)}`, { method: 'GET' }, true)
}

export function getAdminReviewDetail(subjectType: ReviewSubjectType, userId: number) {
  return apiRequest<AdminReviewDetail>(`/api/v1/admin/reviews/${subjectType}/${userId}`, { method: 'GET' }, true)
}

export function approveAdminReview(subjectType: ReviewSubjectType, userId: number) {
  return apiRequest<void>(`/api/v1/admin/reviews/${subjectType}/${userId}/approve`, { method: 'POST' }, true)
}

export function rejectAdminReview(subjectType: ReviewSubjectType, userId: number, reason: string) {
  return apiRequest<void>(`/api/v1/admin/reviews/${subjectType}/${userId}/reject`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  }, true)
}

export function listAdminNotifications(params: { page?: number; pageSize?: number } = {}) {
  return apiRequest<SystemNotificationPagedResult>(`/api/v1/admin/notifications${buildQuery(params)}`, { method: 'GET' }, true)
}

