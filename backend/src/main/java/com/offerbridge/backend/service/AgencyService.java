package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.AgencyDtos;

import java.util.List;

public interface AgencyService {
  AgencyDtos.OrgProfileView getOrgProfile(Long userId);
  AgencyDtos.OrgProfileView createOrgProfile(Long userId, AgencyDtos.OrgProfileUpsertRequest request);
  AgencyDtos.OrgProfileView updateOrgProfile(Long userId, AgencyDtos.OrgProfileUpsertRequest request);
  AgencyDtos.TeamView createTeam(Long userId, AgencyDtos.TeamCreateRequest request);
  List<AgencyDtos.TeamView> listTeams(Long userId);
  AgencyDtos.InvitationView createInvitation(Long userId, AgencyDtos.InvitationCreateRequest request);
  void acceptInvitation(Long userId, String token);
  void updateMyProfile(Long userId, AgencyDtos.MemberProfileUpdateRequest request);
  void updateMyRoles(Long userId, AgencyDtos.MemberRolesUpdateRequest request);
  void updateMyMetrics(Long userId, AgencyDtos.MemberMetricsUpdateRequest request);
  List<AgencyDtos.DiscoveryMemberItem> listDiscoveryMembers(String roleCode, String country, String direction, String city, String serviceTag, String budgetTag);
  AgencyDtos.DiscoveryMemberDetail getDiscoveryMemberDetail(Long memberId);
  List<AgencyDtos.DiscoveryTeamItem> listDiscoveryTeams(String keyword, String country, String direction, String city, String roleCode, String serviceTag);
  AgencyDtos.DiscoveryTeamDetail getDiscoveryTeamDetail(Long teamId);
}
