export type ReviewSubjectType = 'ORG' | 'MEMBER' | 'STUDENT'
export type ReviewStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'UNVERIFIED' | string

export interface AdminReviewListItem {
  userId: number
  subjectType: ReviewSubjectType
  subjectName: string
  phone: string
  orgName?: string | null
  status: ReviewStatus
  rejectReason?: string | null
  submittedAt?: string | null
}

export interface AdminReviewDetail {
  userId: number
  subjectType: ReviewSubjectType
  subjectName: string
  phone: string
  orgName?: string | null
  status: ReviewStatus
  rejectReason?: string | null
  payloadJson?: string | null
  submittedAt?: string | null
}

export interface AdminPagedResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}

