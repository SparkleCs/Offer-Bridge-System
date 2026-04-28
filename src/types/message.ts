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

export interface ChatConversationItem {
  conversationId: string
  studentUserId: number
  agentUserId: number
  agentMemberId: number
  teamId: number
  orgId: number
  teamName: string
  orgName: string
  agentName: string
  agentAvatarUrl?: string | null
  agentJobTitle?: string | null
  studentName?: string | null
  studentSchoolName?: string | null
  studentMajor?: string | null
  studentEducationLevel?: string | null
  studentTargetMajorText?: string | null
  peerName: string
  peerSubtitle: string
  peerAvatarUrl?: string | null
  lastMessage?: string | null
  lastSenderRole?: 'STUDENT' | 'AGENT_MEMBER' | string
  studentMessageCount: number
  agentMessageCount: number
  viewerStarred: boolean
  relatedOrderStatus?: string | null
  relatedOrderId?: number | null
  unreadCount: number
  createdAt: string
  updatedAt: string
}

export interface ChatMessageItem {
  id: string
  conversationId: string
  senderUserId: number
  receiverUserId: number
  senderRole: 'STUDENT' | 'AGENT_MEMBER' | string
  contentType: 'TEXT' | string
  content: string
  status: 'UNREAD' | 'READ' | string
  mine: boolean
  createdAt: string
  readAt?: string | null
}

export interface ChatPagedResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
  unreadCount: number
}

export interface StartChatPayload {
  teamId: number
  greeting: string
}

export interface AgentStartChatPayload {
  studentUserId: number
  teamId: number
  greeting?: string
}

export interface ChatStartResult {
  conversation: ChatConversationItem
  firstMessage: ChatMessageItem
}

export interface SendChatMessagePayload {
  content: string
  contentType?: 'TEXT' | 'IMAGE' | 'FILE' | string
}

export interface ChatUnreadSummary {
  unreadCount: number
}
