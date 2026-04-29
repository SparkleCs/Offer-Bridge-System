import { apiRequest } from './http'
import type {
  ChatConversationItem,
  ChatMessageItem,
  ChatPagedResult,
  ChatStartResult,
  ChatUnreadSummary,
  ChatActionRequest,
  ChatActionRespondPayload,
  ContactExchangeView,
  AgentStartChatPayload,
  MarkSystemNotificationReadPayload,
  MarkSystemNotificationReadResult,
  SendChatMessagePayload,
  StudentAcademicResume,
  StartChatPayload,
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

export function startChat(payload: StartChatPayload) {
  return apiRequest<ChatStartResult>('/api/v1/messages/chats/start', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function agentStartChat(payload: AgentStartChatPayload) {
  return apiRequest<ChatStartResult>('/api/v1/messages/chats/agent-start', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function listChatConversations(params: { page?: number; pageSize?: number; filter?: string } = {}) {
  const search = new URLSearchParams()
  if (params.page) search.append('page', String(params.page))
  if (params.pageSize) search.append('pageSize', String(params.pageSize))
  if (params.filter) search.append('filter', params.filter)
  const query = search.toString()
  return apiRequest<ChatPagedResult<ChatConversationItem>>(`/api/v1/messages/chats${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function listChatMessages(conversationId: string, params: { page?: number; pageSize?: number } = {}) {
  const search = new URLSearchParams()
  if (params.page) search.append('page', String(params.page))
  if (params.pageSize) search.append('pageSize', String(params.pageSize))
  const query = search.toString()
  return apiRequest<ChatPagedResult<ChatMessageItem>>(`/api/v1/messages/chats/${conversationId}/messages${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function sendChatMessage(conversationId: string, payload: SendChatMessagePayload) {
  return apiRequest<ChatMessageItem>(`/api/v1/messages/chats/${conversationId}/messages`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function startChatAction(conversationId: string, payload: ChatActionRequest) {
  return apiRequest<ChatMessageItem>(`/api/v1/messages/chats/${conversationId}/actions`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function respondChatAction(conversationId: string, actionId: string, payload: ChatActionRespondPayload) {
  return apiRequest<ChatMessageItem>(`/api/v1/messages/chats/${conversationId}/actions/${actionId}/respond`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function getStudentResume(conversationId: string) {
  return apiRequest<StudentAcademicResume>(`/api/v1/messages/chats/${conversationId}/student-resume`, { method: 'GET' }, true)
}

export function getExchangedContact(conversationId: string, type: 'PHONE_EXCHANGE' | 'WECHAT_EXCHANGE') {
  return apiRequest<ContactExchangeView>(`/api/v1/messages/chats/${conversationId}/contacts/${type}`, { method: 'GET' }, true)
}

export function uploadChatFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return apiRequest<{ url: string }>('/api/v1/files/upload?bucket=chat', {
    method: 'POST',
    body: formData
  }, true)
}

export function markChatRead(conversationId: string) {
  return apiRequest<MarkSystemNotificationReadResult>(`/api/v1/messages/chats/${conversationId}/read`, {
    method: 'POST'
  }, true)
}

export function starChatConversation(conversationId: string) {
  return apiRequest<ChatConversationItem>(`/api/v1/messages/chats/${conversationId}/star`, { method: 'POST' }, true)
}

export function unstarChatConversation(conversationId: string) {
  return apiRequest<ChatConversationItem>(`/api/v1/messages/chats/${conversationId}/star`, { method: 'DELETE' }, true)
}

export function getChatUnreadSummary() {
  return apiRequest<ChatUnreadSummary>('/api/v1/messages/chats/unread', { method: 'GET' }, true)
}
