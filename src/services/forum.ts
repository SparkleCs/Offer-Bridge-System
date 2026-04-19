import { apiRequest } from './http'
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
  ForumShareView
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
  if (params.channel) search.append('channel', params.channel)
  if (params.keyword) search.append('keyword', params.keyword)
  if (params.page) search.append('page', String(params.page))
  if (params.pageSize) search.append('pageSize', String(params.pageSize))

  const query = search.toString()
  return apiRequest<ForumPostListView>(`/api/v1/forum/posts${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function getForumPostDetail(postId: string) {
  return apiRequest<ForumPost>(`/api/v1/forum/posts/${postId}`, { method: 'GET' }, true)
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
  return apiRequest<ForumCommentListView>(`/api/v1/forum/posts/${postId}/comments`, { method: 'GET' }, true)
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
