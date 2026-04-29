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
  resumeAccessGranted: boolean
  phoneExchangeGranted: boolean
  wechatExchangeGranted: boolean
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
  contentType: 'TEXT' | 'IMAGE' | 'FILE' | 'ACTION_CARD' | string
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

export type ChatActionType = 'RESUME_ACCESS' | 'PHONE_EXCHANGE' | 'WECHAT_EXCHANGE'
export type ChatActionStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export interface ChatActionPayload {
  actionType: ChatActionType
  status: ChatActionStatus
  requesterUserId: number
  requesterRole: 'STUDENT' | 'AGENT_MEMBER' | string
  responderUserId?: number | null
  requestedAt: string
  respondedAt?: string | null
}

export interface ChatActionRequest {
  actionType: ChatActionType
}

export interface ChatActionRespondPayload {
  approved: boolean
}

export interface ContactExchangeView {
  contactType: ChatActionType
  ownContact: string
  peerContact: string
}

export interface ResumeLanguageScore {
  languageType: string
  score: number
}

export interface ResumeTargetCountry {
  countryName: string
}

export interface ResumePublication {
  title: string
  authorRole: string
  journalName: string
  publishedYear: number | null
}

export interface ResumeResearch {
  id?: number
  projectName: string
  startDate: string
  endDate: string
  contentSummary: string
  hasPublication: boolean
  publications: ResumePublication[]
}

export interface ResumeCompetition {
  id?: number
  competitionName: string
  competitionLevel: string
  award: string
  roleDesc: string
  eventDate: string
}

export interface ResumeWork {
  id?: number
  companyName: string
  positionName: string
  startDate: string
  endDate: string
  keywords: string
  contentSummary: string
}

export interface ResumeExchange {
  countryName: string
  universityName: string
  gpaValue: number | null
  majorCourses: string
  startDate: string
  endDate: string
}

export interface StudentAcademicResume {
  studentUserId: number
  displayName: string
  educationLevel?: string | null
  schoolName?: string | null
  major?: string | null
  gpaValue?: number | null
  gpaScale?: string | null
  rankValue?: number | null
  languageScores: ResumeLanguageScore[]
  targetMajorText?: string | null
  intakeTerm?: string | null
  targetCountries: ResumeTargetCountry[]
  exchangeExperience?: ResumeExchange | null
  researchExperiences: ResumeResearch[]
  competitionExperiences: ResumeCompetition[]
  workExperiences: ResumeWork[]
}
