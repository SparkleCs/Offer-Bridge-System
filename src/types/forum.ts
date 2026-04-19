export type ForumChannel = 'EXPERIENCE' | 'OFFER_WALL'
export type ForumNotificationType = 'LIKE' | 'COMMENT' | 'FAVORITE'
export type ForumMode = 'COMMUNITY' | 'MINE'
export type ForumReaction = 'LIKED' | 'FAVORITED'

export interface CreatePostPayload {
  channel: ForumChannel
  title: string
  contentHtml: string
  tags: string[]
}

export interface CreateCommentPayload {
  content: string
}

export interface ForumSearchParams {
  mode?: ForumMode
  channel?: ForumChannel
  reaction?: ForumReaction
  keyword?: string
  page?: number
  pageSize?: number
}

export interface UpdatePostPayload {
  channel: ForumChannel
  title: string
  contentHtml: string
  tags: string[]
}

export interface ForumPost {
  postId: string
  authorUserId: number
  authorDisplayName: string
  channel: ForumChannel
  title: string
  contentHtml: string | null
  summary: string
  tags: string[]
  status: 'PUBLISHED' | 'DELETED'
  likeCount: number
  commentCount: number
  favoriteCount: number
  shareCount: number
  viewerLiked: boolean
  viewerFavorited: boolean
  createdAt: string
  updatedAt: string
}

export interface ForumPostListView {
  total: number
  page: number
  pageSize: number
  items: ForumPost[]
}

export interface ForumComment {
  commentId: string
  postId: string
  authorUserId: number
  content: string
  status: 'PUBLISHED' | 'DELETED'
  createdAt: string
}

export interface ForumCommentListView {
  items: ForumComment[]
}

export interface ForumInteractionState {
  liked: boolean
  favorited: boolean
  likeCount: number
  favoriteCount: number
}

export interface ForumShareView {
  shareUrl: string
  shareCount: number
}

export interface ForumNotification {
  notificationId: string
  receiverUserId: number
  actorUserId: number
  postId: string
  type: ForumNotificationType
  read: boolean
  createdAt: string
}

export interface ForumNotificationListView {
  unreadCount: number
  page: number
  pageSize: number
  items: ForumNotification[]
}

export interface ForumNotificationReadPayload {
  markAll?: boolean
  notificationIds?: string[]
}

export interface ForumNotificationReadResult {
  updatedCount: number
}
