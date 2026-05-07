export type OrderStatus =
  | 'PENDING_QUOTE'
  | 'WAITING_PAYMENT'
  | 'PAID'
  | 'IN_SERVICE'
  | 'COMPLETED'
  | 'REFUND_REQUESTED'
  | 'CLOSED'

export type StageStatus =
  | 'NOT_STARTED'
  | 'IN_PROGRESS'
  | 'WAITING_STUDENT'
  | 'WAITING_AGENT'
  | 'WAITING_CONFIRMATION'
  | 'COMPLETED'

export interface StageAttachment {
  id?: number
  stageId?: number
  fileName: string
  fileUrl: string
  contentType: 'IMAGE' | 'FILE' | string
  mimeType?: string | null
  sizeBytes?: number | null
  uploadedAt?: string | null
}

export interface OrderSummary {
  id: number
  orderNo: string
  teamId: number
  teamNameSnapshot: string
  orgNameSnapshot: string
  serviceTitle?: string | null
  quoteDesc?: string | null
  finalAmount?: number | null
  currency: string
  orderStatus: OrderStatus | string
  paymentStatus: string
  createdAt?: string
  updatedAt?: string
  caseId?: number | null
}

export interface AgentOrderSummary extends OrderSummary {
  studentUserId: number
  studentPhone?: string
  studentName?: string
}

export interface StageItem {
  id: number
  caseId: number
  stageKey: string
  stageName: string
  stageOrder: number
  status: StageStatus | string
  deliverableText?: string | null
  deliverableUrl?: string | null
  attachments?: StageAttachment[]
  studentFeedback?: string | null
  submittedAt?: string | null
  completedAt?: string | null
}

export interface SubmitStagePayload {
  deliverableText: string
  deliverableUrl?: string
  attachments?: StageAttachment[]
}

export interface TodoItem {
  id: number
  caseId: number
  stageId?: number | null
  title: string
  description?: string | null
  ownerRole: 'STUDENT' | 'AGENT' | string
  status: 'OPEN' | 'WAITING_CONFIRMATION' | 'COMPLETED' | string
  completedAt?: string | null
  confirmedAt?: string | null
}

export interface OrderDetail {
  order: OrderSummary | AgentOrderSummary
  stages: StageItem[]
  todos: TodoItem[]
}

export interface PayResult {
  paymentNo: string
  paymentUrl?: string | null
  paymentFormHtml?: string | null
  channel: string
  message: string
}
