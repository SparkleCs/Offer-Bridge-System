import { apiRequest } from './http'
import type {
  AgencyOrgProfile,
  AgencyTeam,
  CreateTeamPayload,
  DiscoveryMemberDetail,
  DiscoveryMemberItem,
  DiscoveryTeamDetail,
  DiscoveryTeamItem,
  InvitationPayload,
  InvitationResult,
  MemberMetricsPayload,
  MemberProfilePayload,
  MemberRolesPayload
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

export function createAgencyTeam(payload: CreateTeamPayload) {
  return apiRequest<AgencyTeam>('/api/v1/agency/teams', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function listAgencyTeams() {
  return apiRequest<AgencyTeam[]>('/api/v1/agency/teams', { method: 'GET' }, true)
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
