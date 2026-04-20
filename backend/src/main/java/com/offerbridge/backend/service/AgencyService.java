package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.AgencyDtos;

import java.util.List;

public interface AgencyService {
  AgencyDtos.OrgProfileView getOrgProfile(Long userId);
  AgencyDtos.OrgProfileView createOrgProfile(Long userId, AgencyDtos.OrgProfileUpsertRequest request);
  AgencyDtos.OrgProfileView updateOrgProfile(Long userId, AgencyDtos.OrgProfileUpsertRequest request);
  AgencyDtos.OrgVerificationView getOrgVerification(Long userId);
  AgencyDtos.OrgVerificationView submitOrgVerification(Long userId, AgencyDtos.OrgVerificationSubmitRequest request);
  AgencyDtos.OrgVerificationView updateOrgVerification(Long userId, AgencyDtos.OrgVerificationSubmitRequest request);
  AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem> listOrgMembers(Long userId, int page, int pageSize, String keyword, String status);
  AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem> listPermissionMembers(Long userId, int page, int pageSize, String keyword);
  AgencyDtos.MemberAdminItem createOrgMember(Long userId, AgencyDtos.MemberCreateRequest request);
  void updateOrgMember(Long userId, Long memberId, AgencyDtos.MemberProfileUpdateRequest request);
  void updateOrgMemberRoles(Long userId, Long memberId, AgencyDtos.MemberRolesUpdateRequest request);
  void updateOrgMemberStatus(Long userId, Long memberId, AgencyDtos.MemberStatusUpdateRequest request);
  void softDeleteOrgMember(Long userId, Long memberId);
  void updateOrgMemberPermissions(Long userId, Long memberId, AgencyDtos.MemberPermissionsUpdateRequest request);
  AgencyDtos.MemberWorkbenchAccessView getMyWorkbenchAccess(Long userId);
  AgencyDtos.TeamView createTeam(Long userId, AgencyDtos.TeamCreateRequest request);
  List<AgencyDtos.TeamView> listTeams(Long userId);
  AgencyDtos.InvitationView createInvitation(Long userId, AgencyDtos.InvitationCreateRequest request);
  void acceptInvitation(Long userId, String token);
  AgencyDtos.MemberSelfProfileView getMyProfile(Long userId);
  AgencyDtos.MemberVerificationStatusView getMyVerificationStatus(Long userId);
  void updateMyProfile(Long userId, AgencyDtos.MemberProfileUpdateRequest request);
  void updateMyAvatar(Long userId, AgencyDtos.MemberAvatarUpdateRequest request);
  void submitMyVerification(Long userId, AgencyDtos.MemberVerificationSubmitRequest request);
  void submitMyProfileForAudit(Long userId);
  void updateMyRoles(Long userId, AgencyDtos.MemberRolesUpdateRequest request);
  void updateMyMetrics(Long userId, AgencyDtos.MemberMetricsUpdateRequest request);
  List<AgencyDtos.DiscoveryMemberItem> listDiscoveryMembers(String roleCode, String country, String direction, String city, String serviceTag, String budgetTag);
  AgencyDtos.DiscoveryMemberDetail getDiscoveryMemberDetail(Long memberId);
  List<AgencyDtos.DiscoveryTeamItem> listDiscoveryTeams(String keyword, String country, String direction, String city, String roleCode, String serviceTag);
  AgencyDtos.DiscoveryTeamDetail getDiscoveryTeamDetail(Long teamId);
}
