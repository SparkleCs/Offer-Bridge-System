export interface SystemNotificationItem {
  id: number
  userId: number
  type: string
  title: string
  content: string
  status: 'UNREAD' | 'READ' | string
  relatedType?: string | null
  relatedId?: string | null
  createdAt: string
  readAt?: string | null
}

export interface SystemNotificationPagedResult {
  records: SystemNotificationItem[]
  total: number
  page: number
  pageSize: number
  unreadCount: number
}

export interface MarkSystemNotificationReadPayload {
  markAll?: boolean
  ids?: number[]
}

export interface MarkSystemNotificationReadResult {
  updatedCount: number
}

