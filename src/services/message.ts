import { apiRequest } from './http'
import type {
  MarkSystemNotificationReadPayload,
  MarkSystemNotificationReadResult,
  SystemNotificationPagedResult
} from '../types/message'

export function listSystemNotifications(params: { page?: number; pageSize?: number } = {}) {
  const search = new URLSearchParams()
  if (params.page) search.append('page', String(params.page))
  if (params.pageSize) search.append('pageSize', String(params.pageSize))
  const query = search.toString()
  return apiRequest<SystemNotificationPagedResult>(`/api/v1/messages/system${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function markSystemNotificationsRead(payload: MarkSystemNotificationReadPayload) {
  return apiRequest<MarkSystemNotificationReadResult>('/api/v1/messages/system/read', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

