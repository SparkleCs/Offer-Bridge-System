import { apiRequest } from './http'
import type {
  AgencyOrgProfile,
  AgentStudentSearchItem,
  AgencyTeam,
  CreateTeamPayload,
  DiscoveryMemberDetail,
  DiscoveryMemberItem,
  DiscoveryTeamDetail,
  DiscoveryTeamItem,
  InvitationPayload,
  InvitationResult,
  MemberMetricsPayload,
  MemberAvatarUpdatePayload,
  MemberProfilePayload,
  MemberRolesPayload,
  MemberSelfProfile,
  MemberVerificationStatus,
  MemberVerificationSubmitPayload,
  MemberWorkbenchAccess,
  OrgMemberCreatePayload,
  OrgMemberItem,
  OrgMemberPermissionsPayload,
  OrgMemberStatusPayload,
  PagedResult,
  TeamProductDetailView,
  TeamProductOrgMemberItem,
  TeamProductSummaryItem,
  TeamProductUpsertPayload,
  OrgVerificationPayload,
  OrgVerificationView
} from '../types/agency'

export function getAgencyOrgProfile() {
  return apiRequest<AgencyOrgProfile | null>('/api/v1/agency/org/profile', { method: 'GET' }, true)
}

export function createAgencyOrgProfile(payload: AgencyOrgProfile) {
  return apiRequest<AgencyOrgProfile>('/api/v1/agency/org/profile', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function updateAgencyOrgProfile(payload: AgencyOrgProfile) {
  return apiRequest<AgencyOrgProfile>('/api/v1/agency/org/profile', {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function getOrgVerification() {
  return apiRequest<OrgVerificationView>('/api/v1/agency/org/verification', { method: 'GET' }, true)
}

export function submitOrgVerification(payload: OrgVerificationPayload) {
  return apiRequest<OrgVerificationView>('/api/v1/agency/org/verification', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function updateOrgVerification(payload: OrgVerificationPayload) {
  return apiRequest<OrgVerificationView>('/api/v1/agency/org/verification', {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function listOrgMembers(params: {
  page?: number
  pageSize?: number
  keyword?: string
  status?: string
} = {}) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') search.append(key, String(value))
  })
  const query = search.toString()
  return apiRequest<PagedResult<OrgMemberItem>>(`/api/v1/agency/members${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function listPermissionMembers(params: {
  page?: number
  pageSize?: number
  keyword?: string
} = {}) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') search.append(key, String(value))
  })
  const query = search.toString()
  return apiRequest<PagedResult<OrgMemberItem>>(`/api/v1/agency/permissions/members${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function createOrgMember(payload: OrgMemberCreatePayload) {
  return apiRequest<OrgMemberItem>('/api/v1/agency/members', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function updateOrgMember(memberId: number, payload: MemberProfilePayload) {
  return apiRequest<void>(`/api/v1/agency/members/${memberId}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function updateOrgMemberRoles(memberId: number, payload: MemberRolesPayload) {
  return apiRequest<void>(`/api/v1/agency/members/${memberId}/roles`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function updateOrgMemberStatus(memberId: number, payload: OrgMemberStatusPayload) {
  return apiRequest<void>(`/api/v1/agency/members/${memberId}/status`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function updateOrgMemberPermissions(memberId: number, payload: OrgMemberPermissionsPayload) {
  return apiRequest<void>(`/api/v1/agency/members/${memberId}/permissions`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function softDeleteOrgMember(memberId: number) {
  return apiRequest<void>(`/api/v1/agency/members/${memberId}/soft-delete`, { method: 'PUT' }, true)
}

export function createAgencyTeam(payload: CreateTeamPayload) {
  return apiRequest<AgencyTeam>('/api/v1/agency/teams', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function listAgencyTeams() {
  return apiRequest<AgencyTeam[]>('/api/v1/agency/teams', { method: 'GET' }, true)
}

export function listTeamProducts() {
  return apiRequest<TeamProductSummaryItem[]>('/api/v1/agency/team-products', { method: 'GET' }, true)
}

export function getTeamProduct(teamId: number) {
  return apiRequest<TeamProductDetailView>(`/api/v1/agency/team-products/${teamId}`, { method: 'GET' }, true)
}

export function createTeamProduct(payload: TeamProductUpsertPayload) {
  return apiRequest<TeamProductDetailView>('/api/v1/agency/team-products', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function updateTeamProduct(teamId: number, payload: TeamProductUpsertPayload) {
  return apiRequest<TeamProductDetailView>(`/api/v1/agency/team-products/${teamId}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function publishTeamProduct(teamId: number) {
  return apiRequest<void>(`/api/v1/agency/team-products/${teamId}/publish`, {
    method: 'POST'
  }, true)
}

export function listTeamProductOrgMembers(keyword?: string) {
  const query = keyword && keyword.trim() ? `?keyword=${encodeURIComponent(keyword.trim())}` : ''
  return apiRequest<TeamProductOrgMemberItem[]>(`/api/v1/agency/team-products/org-members${query}`, { method: 'GET' }, true)
}

export function createAgencyInvitation(payload: InvitationPayload) {
  return apiRequest<InvitationResult>('/api/v1/agency/invitations', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function acceptAgencyInvitation(token: string) {
  return apiRequest<void>(`/api/v1/agency/invitations/${token}/accept`, { method: 'POST' }, true)
}

export function updateMyAgencyProfile(payload: MemberProfilePayload) {
  return apiRequest<void>('/api/v1/agency/members/me/profile', {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function getMyAgencyProfile() {
  return apiRequest<MemberSelfProfile>('/api/v1/agency/members/me/profile', { method: 'GET' }, true)
}

export function updateMyAvatar(payload: MemberAvatarUpdatePayload) {
  return apiRequest<void>('/api/v1/agency/members/me/avatar', {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function submitMyProfileForAudit() {
  return apiRequest<void>('/api/v1/agency/members/me/profile/submit', { method: 'PUT' }, true)
}

export function submitMyMemberVerification(payload: MemberVerificationSubmitPayload) {
  return apiRequest<void>('/api/v1/agency/members/me/verification/submit', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function getMyMemberVerificationStatus() {
  return apiRequest<MemberVerificationStatus>('/api/v1/agency/members/me/verification/status', { method: 'GET' }, true)
}

export function updateMyAgencyRoles(payload: MemberRolesPayload) {
  return apiRequest<void>('/api/v1/agency/members/me/roles', {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function updateMyAgencyMetrics(payload: MemberMetricsPayload) {
  return apiRequest<void>('/api/v1/agency/members/me/metrics', {
    method: 'PUT',
    body: JSON.stringify(payload)
  }, true)
}

export function getMyWorkbenchAccess() {
  return apiRequest<MemberWorkbenchAccess>('/api/v1/agency/members/me/workbench-access', { method: 'GET' }, true)
}

export function searchAgentStudents(params: {
  page?: number
  pageSize?: number
  keyword?: string
  country?: string
  educationLevel?: string
  scoreBucket?: string
  subjectCategoryCode?: string
} = {}) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value) search.append(key, String(value))
  })
  const query = search.toString()
  return apiRequest<PagedResult<AgentStudentSearchItem>>(`/api/v1/agency/student-search${query ? `?${query}` : ''}`, { method: 'GET' }, true)
}

export function listDiscoveryMembers(params: {
  roleCode?: string
  country?: string
  direction?: string
  city?: string
  serviceTag?: string
  budgetTag?: string
} = {}) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value) search.append(key, value)
  })
  const query = search.toString()
  return apiRequest<DiscoveryMemberItem[]>(`/api/v1/agency/discovery/members${query ? `?${query}` : ''}`, { method: 'GET' }, false)
}

export function getDiscoveryMemberDetail(memberId: number) {
  return apiRequest<DiscoveryMemberDetail>(`/api/v1/agency/discovery/members/${memberId}`, { method: 'GET' }, false)
}

export function listDiscoveryTeams(params: {
  keyword?: string
  country?: string
  direction?: string
  city?: string
  roleCode?: string
  serviceTag?: string
} = {}) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value) search.append(key, value)
  })
  const query = search.toString()
  return apiRequest<DiscoveryTeamItem[]>(`/api/v1/agency/discovery/teams${query ? `?${query}` : ''}`, { method: 'GET' }, false)
}

export function getDiscoveryTeamDetail(teamId: number) {
  return apiRequest<DiscoveryTeamDetail>(`/api/v1/agency/discovery/teams/${teamId}`, { method: 'GET' }, false)
}

export async function uploadFile(file: File, bucket: 'org-verification' | 'member-verification' | 'student-verification' | 'avatar' | 'general' = 'general') {
  const form = new FormData()
  form.append('file', file)
  return apiRequest<{ url: string }>(`/api/v1/files/upload?bucket=${encodeURIComponent(bucket)}`, {
    method: 'POST',
    body: form
  }, true)
}
