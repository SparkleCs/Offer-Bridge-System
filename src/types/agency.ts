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

export interface AgencyTeam {
  id: number
  teamName: string
  teamType?: string | null
  teamIntro?: string | null
  serviceCountryScope: string
  serviceMajorScope: string
}

export interface CreateTeamPayload {
  teamName: string
  teamType?: string
  teamIntro?: string
  serviceCountryScope: string
  serviceMajorScope: string
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
