export interface AgencyOrgProfile {
  id?: number
  orgName: string
  brandName?: string | null
  countryCode: string
  provinceOrState: string
  city: string
  district?: string | null
  addressLine: string
  logoUrl?: string | null
  coverImageUrl?: string | null
  officeEnvironmentImages?: string | null
  contactPhone: string
  contactEmail?: string | null
  websiteUrl?: string | null
  socialLinks?: string | null
  foundedYear?: number | null
  teamSizeRange?: string | null
  serviceMode: string
  orgIntro: string
  orgSlogan?: string | null
  verificationStatus?: string
}

export type OrgVerificationStatus = 'PENDING' | 'APPROVED' | 'REJECTED'
export type AgentPermissionCode = 'CAN_CHAT_STUDENT' | 'CAN_PUBLISH_PACKAGE' | string
export type OrgAdminMenuKey = 'verification' | 'members' | 'permissions' | 'company'
export type AgentWorkbenchMenuKey = 'team-products' | 'students' | 'recommend' | 'search' | 'communication' | 'profile' | 'data'

export interface OrgVerificationPayload {
  licenseNo: string
  legalPersonName: string
  licenseImageUrl: string
  legalPersonIdImageUrl: string
  corporateAccountName: string
  corporateBankName: string
  corporateBankAccountNo: string
  corporateAccountProofImageUrl?: string
  officeEnvironmentImageUrls?: string
  adminRealNameImageUrl: string
  adminEmploymentProofImageUrl: string
  remark?: string
}

export interface OrgVerificationView {
  verificationStatus: OrgVerificationStatus
  recordStatus?: string
  payloadJson?: string
  rejectReason?: string
  submittedAt?: string
}

export interface AgencyTeam {
  id: number
  teamName: string
  teamType?: string | null
  teamIntro?: string | null
  serviceCountryScope: string
  serviceMajorScope: string
  priceMin?: number
  priceMax?: number
  publishStatus?: 'DRAFT' | 'PUBLISHED' | string
  updatedAt?: string
}

export interface CreateTeamPayload {
  teamName: string
  teamType?: string
  teamIntro?: string
  serviceCountryScope: string
  serviceMajorScope: string
}

export interface TeamProductUpsertPayload {
  teamName: string
  teamType?: string
  teamIntro?: string
  serviceCountryScope: string
  serviceMajorScope: string
  priceMin: number
  priceMax: number
  publisherMemberIds: number[]
}

export interface TeamProductSummaryItem {
  teamId: number
  teamName: string
  teamType?: string | null
  publishStatus: 'DRAFT' | 'PUBLISHED' | string
  priceMin: number
  priceMax: number
  updatedAt?: string
}

export interface TeamProductOrgMemberItem {
  memberId: number
  displayName: string
  avatarUrl?: string | null
  jobTitle?: string | null
  roleCode?: string | null
  verifiedBadgeStatus?: string
}

export interface TeamProductPublisherItem {
  memberId: number
  displayName: string
  avatarUrl?: string | null
  jobTitle?: string | null
  roleCode?: string | null
  bio?: string | null
  yearsOfExperience?: number
  educationLevel?: string | null
  specialCountries?: string | null
  specialDirections?: string | null
}

export interface TeamProductDetailView {
  teamId: number
  teamName: string
  teamType?: string | null
  teamIntro?: string | null
  serviceCountryScope: string
  serviceMajorScope: string
  priceMin: number
  priceMax: number
  publishStatus: 'DRAFT' | 'PUBLISHED' | string
  updatedAt?: string
  publisherMembers: TeamProductPublisherItem[]
}

export interface InvitationPayload {
  teamId: number
  email: string
  inviteeName?: string
  roleHint?: string
}

export interface InvitationResult {
  id: number
  email: string
  status: string
  token: string
}

export interface MemberProfilePayload {
  displayName: string
  wechatId?: string
  jobTitle: string
  educationLevel: string
  graduatedSchool: string
  major?: string
  yearsOfExperience: number
  specialCountries: string
  specialDirections: string
  bio: string
  serviceStyleTags?: string
  publicStatus: 'PUBLIC' | 'PRIVATE'
}

export interface MemberRoleItem {
  roleCode: string
  isPrimary: boolean
}

export interface MemberRolesPayload {
  roles: MemberRoleItem[]
}

export interface MemberMetricsPayload {
  caseCount: number
  successRate: number
  avgRating: number
  responseEfficiencyScore: number
  serviceTags?: string
  budgetTags?: string
}

export interface MemberSelfProfile {
  memberId: number
  orgId: number
  displayName: string
  avatarUrl?: string | null
  wechatId?: string | null
  jobTitle: string
  educationLevel: string
  graduatedSchool: string
  major?: string | null
  yearsOfExperience: number
  specialCountries: string
  specialDirections: string
  bio: string
  serviceStyleTags?: string | null
  publicStatus: 'PUBLIC' | 'PRIVATE'
  profileAuditStatus?: string
  roleCodes?: string[]
}

export interface MemberAvatarUpdatePayload {
  avatarUrl: string
}

export interface MemberVerificationSubmitPayload {
  idCardImageUrl: string
  employmentProofImageUrl: string
  educationProofImageUrl: string
}

export interface MemberVerificationStatus {
  status: 'UNVERIFIED' | 'PENDING' | 'APPROVED' | 'REJECTED' | string
  rejectReason?: string | null
  payloadJson?: string | null
  submittedAt?: string | null
}

export interface OrgMemberItem {
  memberId: number
  userId: number
  phone: string
  displayName: string
  jobTitle: string
  educationLevel: string
  graduatedSchool: string
  yearsOfExperience: number
  publicStatus: 'PUBLIC' | 'PRIVATE'
  verifiedBadgeStatus: string
  profileAuditStatus: 'DRAFT' | 'PENDING' | 'APPROVED' | 'REJECTED' | string
  memberStatus: 'ACTIVE' | 'DELETED' | string
  accountStatus: 'ACTIVE' | 'DISABLED'
  roleCodes: string[]
  permissions: AgentPermissionCode[]
}

export interface OrgMemberCreatePayload {
  phone: string
  displayName: string
  jobTitle: string
  permissions?: AgentPermissionCode[]
}

export interface OrgMemberStatusPayload {
  status: 'ACTIVE' | 'DISABLED'
}

export interface OrgMemberPermissionsPayload {
  permissions: AgentPermissionCode[]
}

export interface PagedResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}

export interface MemberWorkbenchAccess {
  orgVerificationStatus: OrgVerificationStatus
  memberVerificationStatus: 'UNVERIFIED' | 'PENDING' | 'APPROVED' | 'REJECTED' | string
  permissions: AgentPermissionCode[]
  canChatStudent: boolean
  canPublishPackage: boolean
  canDoCoreActions: boolean
  blockedReason?: string
}

export interface AgentStudentSearchItem {
  studentUserId: number
  displayName: string
  educationLevel?: string | null
  schoolName?: string | null
  major?: string | null
  gpaValue?: number | null
  gpaScale?: string | null
  languageSummary?: string | null
  targetCountries?: string | null
  targetMajorText?: string | null
  intakeTerm?: string | null
}

export interface DiscoveryMemberItem {
  memberId: number
  displayName: string
  jobTitle: string
  primaryRole: string
  orgName: string
  teamName: string
  city: string
  bio: string
  specialCountries: string
  specialDirections: string
  yearsOfExperience: number
  educationLevel: string
  caseCount: number
  successRate: number
  avgRating: number
  responseEfficiencyScore: number
  serviceTags?: string
  budgetTags?: string
  verifiedBadgeStatus: string
  logoUrl?: string
}

export interface DiscoveryMemberDetail {
  member: DiscoveryMemberItem
  org: AgencyOrgProfile
  roleCodes: string[]
}

export interface DiscoveryTeamItem {
  teamId: number
  teamName: string
  teamIntro: string
  orgName: string
  city: string
  serviceCountryScope: string
  serviceMajorScope: string
  caseCount: number
  successRate: number
  avgRating: number
  responseEfficiencyScore: number
  priceTextPlaceholder: string
}

export interface DiscoveryTeamMemberItem {
  memberId: number
  displayName: string
  jobTitle: string
  roleCode: string
  bio: string
  specialCountries: string
  specialDirections: string
  yearsOfExperience: number
  educationLevel: string
}

export interface DiscoveryTeamDetail {
  teamId: number
  teamName: string
  teamType: string
  teamIntro: string
  orgName: string
  city: string
  serviceCountryScope: string
  serviceMajorScope: string
  caseCount: number
  successRate: number
  avgRating: number
  responseEfficiencyScore: number
  priceTextPlaceholder: string
  packagePlaceholderTitle: string
  packagePlaceholderDesc: string
  org: AgencyOrgProfile | null
  members: DiscoveryTeamMemberItem[]
}
