export interface OrderReviewTarget {
  orderId: number
  memberId: number
  displayName: string
  avatarUrl?: string | null
  jobTitle?: string | null
  roleCode: string
  reviewed: boolean
}

export interface OrderReviewStatus {
  canReview: boolean
  reason: string
  targets: OrderReviewTarget[]
}

export interface MemberReviewSubmitItem {
  memberId: number
  professionalScore: number
  communicationScore: number
  materialScore: number
  transparencyScore: number
  responsibilityScore: number
  npsScore?: number | null
  commentText?: string
  anonymous: boolean
}

export interface SubmitOrderReviewPayload {
  reviews: MemberReviewSubmitItem[]
}

export interface RatingSummary {
  teamId?: number | null
  totalScore: number
  studentReviewScore: number
  offerOutcomeScore: number
  processPerformanceScore: number
  platformTrustScore: number
  reviewCount: number
  positiveRate: number
  confidenceLabel: string
}

export interface TeamMemberReviewSummary {
  memberId: number
  displayName: string
  avatarUrl?: string | null
  jobTitle?: string | null
  roleCode: string
  ratingScore: number
  avgRating: number
  reviewCount: number
  positiveRate: number
  keywordSummary: string
}

export interface MemberReviewItem {
  reviewId: number
  memberId: number
  studentName: string
  overallRating: number
  professionalScore: number
  communicationScore: number
  materialScore: number
  transparencyScore: number
  responsibilityScore: number
  npsScore?: number | null
  commentText?: string | null
  createdAt: string
}

export interface AgencyDashboard {
  orgSummary: RatingSummary
  memberRankings: TeamMemberReviewSummary[]
  teamSummaries: RatingSummary[]
}
