import { apiRequest, apiRequestOptionalAuth } from './http'
import type {
  ForumComment,
  CreateCommentPayload,
  CreatePostPayload,
  ForumCommentListView,
  ForumInteractionState,
  ForumNotificationListView,
  ForumNotificationReadPayload,
  ForumNotificationReadResult,
  ForumPost,
  ForumPostListView,
  ForumSearchParams,
  ForumShareView,
  UpdatePostPayload
} from '../types/forum'

export function createForumPost(payload: CreatePostPayload) {
  return apiRequest<ForumPost>(
    '/api/v1/forum/posts',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    true
  )
}

export function listForumPosts(params: ForumSearchParams = {}) {
  const search = new URLSearchParams()
  if (params.mode) search.append('mode', params.mode)
  if (params.channel) search.append('channel', params.channel)
  if (params.reaction) search.append('reaction', params.reaction)
  if (params.keyword) search.append('keyword', params.keyword)
  if (params.page) search.append('page', String(params.page))
  if (params.pageSize) search.append('pageSize', String(params.pageSize))

  const query = search.toString()
  const path = `/api/v1/forum/posts${query ? `?${query}` : ''}`
  if (params.mode === 'MINE') {
    return apiRequest<ForumPostListView>(path, { method: 'GET' }, true)
  }
  return apiRequestOptionalAuth<ForumPostListView>(path, { method: 'GET' })
}

export function getForumPostDetail(postId: string) {
  return apiRequestOptionalAuth<ForumPost>(`/api/v1/forum/posts/${postId}`, { method: 'GET' })
}

export function updateForumPost(postId: string, payload: UpdatePostPayload) {
  return apiRequest<ForumPost>(`/api/v1/forum/posts/${postId}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function deleteForumPost(postId: string) {
  return apiRequest<void>(`/api/v1/forum/posts/${postId}`, { method: 'DELETE' }, true)
}

export function likeForumPost(postId: string) {
  return apiRequest<ForumInteractionState>(`/api/v1/forum/posts/${postId}/like`, { method: 'POST' }, true)
}

export function unlikeForumPost(postId: string) {
  return apiRequest<ForumInteractionState>(`/api/v1/forum/posts/${postId}/like`, { method: 'DELETE' }, true)
}

export function favoriteForumPost(postId: string) {
  return apiRequest<ForumInteractionState>(`/api/v1/forum/posts/${postId}/favorite`, { method: 'POST' }, true)
}

export function unfavoriteForumPost(postId: string) {
  return apiRequest<ForumInteractionState>(`/api/v1/forum/posts/${postId}/favorite`, { method: 'DELETE' }, true)
}

export function addForumComment(postId: string, payload: CreateCommentPayload) {
  return apiRequest<ForumComment>(`/api/v1/forum/posts/${postId}/comments`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function listForumComments(postId: string) {
  return apiRequestOptionalAuth<ForumCommentListView>(`/api/v1/forum/posts/${postId}/comments`, { method: 'GET' })
}

export function shareForumPost(postId: string) {
  return apiRequest<ForumShareView>(`/api/v1/forum/posts/${postId}/share`, { method: 'POST' }, true)
}

export function listForumNotifications(params: { page?: number; pageSize?: number } = {}) {
  const search = new URLSearchParams()
  if (params.page) search.append('page', String(params.page))
  if (params.pageSize) search.append('pageSize', String(params.pageSize))
  const query = search.toString()
  return apiRequest<ForumNotificationListView>(`/api/v1/forum/notifications${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function markForumNotificationsRead(payload: ForumNotificationReadPayload) {
  return apiRequest<ForumNotificationReadResult>('/api/v1/forum/notifications/read', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}
