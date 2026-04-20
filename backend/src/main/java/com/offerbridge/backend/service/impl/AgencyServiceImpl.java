package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.entity.AgencyInvitation;
import com.offerbridge.backend.entity.AgencyMemberMetrics;
import com.offerbridge.backend.entity.AgencyMemberProfile;
import com.offerbridge.backend.entity.AgencyMemberVerificationMaterial;
import com.offerbridge.backend.entity.AgencyOrg;
import com.offerbridge.backend.entity.AgencyTeam;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.entity.VerificationRecord;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AgencyInvitationMapper;
import com.offerbridge.backend.mapper.AgencyMemberMetricsMapper;
import com.offerbridge.backend.mapper.AgencyMemberPermissionRelMapper;
import com.offerbridge.backend.mapper.AgencyMemberProfileMapper;
import com.offerbridge.backend.mapper.AgencyMemberVerificationMaterialMapper;
import com.offerbridge.backend.mapper.AgencyMemberRoleRelMapper;
import com.offerbridge.backend.mapper.AgencyOrgMapper;
import com.offerbridge.backend.mapper.AgencyTeamMapper;
import com.offerbridge.backend.mapper.AgencyTeamMemberRelMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.mapper.VerificationRecordMapper;
import com.offerbridge.backend.service.AgencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AgencyServiceImpl implements AgencyService {
  private final AgencyOrgMapper agencyOrgMapper;
  private final AgencyTeamMapper agencyTeamMapper;
  private final AgencyInvitationMapper agencyInvitationMapper;
  private final AgencyMemberProfileMapper agencyMemberProfileMapper;
  private final AgencyMemberVerificationMaterialMapper agencyMemberVerificationMaterialMapper;
  private final AgencyTeamMemberRelMapper agencyTeamMemberRelMapper;
  private final AgencyMemberRoleRelMapper agencyMemberRoleRelMapper;
  private final AgencyMemberMetricsMapper agencyMemberMetricsMapper;
  private final AgencyMemberPermissionRelMapper agencyMemberPermissionRelMapper;
  private final VerificationRecordMapper verificationRecordMapper;
  private final UserAccountMapper userAccountMapper;
  private final ObjectMapper objectMapper;

  public AgencyServiceImpl(AgencyOrgMapper agencyOrgMapper,
                           AgencyTeamMapper agencyTeamMapper,
                           AgencyInvitationMapper agencyInvitationMapper,
                           AgencyMemberProfileMapper agencyMemberProfileMapper,
                           AgencyMemberVerificationMaterialMapper agencyMemberVerificationMaterialMapper,
                           AgencyTeamMemberRelMapper agencyTeamMemberRelMapper,
                           AgencyMemberRoleRelMapper agencyMemberRoleRelMapper,
                           AgencyMemberMetricsMapper agencyMemberMetricsMapper,
                           AgencyMemberPermissionRelMapper agencyMemberPermissionRelMapper,
                           VerificationRecordMapper verificationRecordMapper,
                           UserAccountMapper userAccountMapper,
                           ObjectMapper objectMapper) {
    this.agencyOrgMapper = agencyOrgMapper;
    this.agencyTeamMapper = agencyTeamMapper;
    this.agencyInvitationMapper = agencyInvitationMapper;
    this.agencyMemberProfileMapper = agencyMemberProfileMapper;
    this.agencyMemberVerificationMaterialMapper = agencyMemberVerificationMaterialMapper;
    this.agencyTeamMemberRelMapper = agencyTeamMemberRelMapper;
    this.agencyMemberRoleRelMapper = agencyMemberRoleRelMapper;
    this.agencyMemberMetricsMapper = agencyMemberMetricsMapper;
    this.agencyMemberPermissionRelMapper = agencyMemberPermissionRelMapper;
    this.verificationRecordMapper = verificationRecordMapper;
    this.userAccountMapper = userAccountMapper;
    this.objectMapper = objectMapper;
  }

  @Override
  public AgencyDtos.OrgProfileView getOrgProfile(Long userId) {
    AgencyOrg org = resolveOrgForUser(userId);
    if (org == null) {
      return null;
    }
    return toOrgView(org);
  }

  @Override
  @Transactional
  public AgencyDtos.OrgProfileView createOrgProfile(Long userId, AgencyDtos.OrgProfileUpsertRequest request) {
    UserAccount user = requireUser(userId);
    requireRole(user, "AGENT_ORG");
    if (agencyOrgMapper.findByAdminUserId(userId) != null) {
      throw new BizException("BIZ_BAD_REQUEST", "机构档案已存在，请改用更新接口");
    }
    AgencyOrg entity = buildOrgEntity(userId, request);
    agencyOrgMapper.insertOne(entity);
    return toOrgView(entity);
  }

  @Override
  @Transactional
  public AgencyDtos.OrgProfileView updateOrgProfile(Long userId, AgencyDtos.OrgProfileUpsertRequest request) {
    UserAccount user = requireUser(userId);
    requireRole(user, "AGENT_ORG");
    AgencyOrg existing = agencyOrgMapper.findByAdminUserId(userId);
    if (existing == null) {
      throw new BizException("BIZ_NOT_FOUND", "机构档案不存在，请先创建");
    }
    AgencyOrg update = buildOrgEntity(userId, request);
    update.setId(existing.getId());
    agencyOrgMapper.updateOne(update);
    return toOrgView(agencyOrgMapper.findById(existing.getId()));
  }

  @Override
  public AgencyDtos.OrgVerificationView getOrgVerification(Long userId) {
    AgencyOrg org = requireOrgByAdmin(userId);
    VerificationRecord record = verificationRecordMapper.findOne(userId, "AGENT_ORG");
    return toOrgVerificationView(org, record);
  }

  @Override
  @Transactional
  public AgencyDtos.OrgVerificationView submitOrgVerification(Long userId, AgencyDtos.OrgVerificationSubmitRequest request) {
    return upsertOrgVerification(userId, request);
  }

  @Override
  @Transactional
  public AgencyDtos.OrgVerificationView updateOrgVerification(Long userId, AgencyDtos.OrgVerificationSubmitRequest request) {
    return upsertOrgVerification(userId, request);
  }

  @Override
  public AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem> listOrgMembers(Long userId, int page, int pageSize, String keyword, String status) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    List<AgencyDtos.MemberAdminItem> members = agencyMemberProfileMapper.listByOrgId(org.getId());
    List<AgencyDtos.MemberAdminItem> filtered = new ArrayList<>();
    for (AgencyDtos.MemberAdminItem item : members) {
      item.setRoleCodes(agencyMemberRoleRelMapper.listRoleCodesByMemberId(item.getMemberId()));
      item.setPermissions(agencyMemberPermissionRelMapper.listPermissionCodesByMemberId(item.getMemberId()));
      if ("DELETED".equals(item.getMemberStatus())) continue;
      if (status != null && !status.isBlank() && !status.equalsIgnoreCase(item.getAccountStatus())) continue;
      if (keyword != null && !keyword.isBlank()) {
        String k = keyword.trim();
        boolean hit = contains(item.getDisplayName(), k) || contains(item.getPhone(), k) || contains(item.getJobTitle(), k);
        if (!hit) continue;
      }
      filtered.add(item);
    }
    filtered.sort(Comparator.comparing(AgencyDtos.MemberAdminItem::getMemberId).reversed());
    return toPaged(filtered, page, pageSize);
  }

  @Override
  public AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem> listPermissionMembers(Long userId, int page, int pageSize, String keyword) {
    return listOrgMembers(userId, page, pageSize, keyword, null);
  }

  @Override
  @Transactional
  public AgencyDtos.MemberAdminItem createOrgMember(Long userId, AgencyDtos.MemberCreateRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);

    UserAccount user = userAccountMapper.findByPhone(request.getPhone());
    if (user == null) {
      insertUserCompat(request.getPhone().trim(), "AGENT_MEMBER");
      user = userAccountMapper.findByPhone(request.getPhone().trim());
    } else if ("STUDENT".equals(user.getRole()) || "AGENT_ORG".equals(user.getRole())) {
      throw new BizException("BIZ_BAD_REQUEST", "该手机号已绑定其他账号类型");
    } else if (!"ACTIVE".equals(user.getStatus())) {
      userAccountMapper.updateStatus(user.getId(), "ACTIVE");
      user = userAccountMapper.findById(user.getId());
    }
    if (!"AGENT_MEMBER".equals(user.getRole())) {
      userAccountMapper.updateRole(user.getId(), "AGENT_MEMBER");
    }

    AgencyMemberProfile existing = agencyMemberProfileMapper.findByUserId(user.getId());
    if (existing != null && !org.getId().equals(existing.getOrgId())) {
      throw new BizException("BIZ_FORBIDDEN", "该手机号成员已归属其他机构");
    }
    if (existing != null) {
      throw new BizException("BIZ_BAD_REQUEST", "该成员已在当前机构中");
    }

    AgencyMemberProfile member = new AgencyMemberProfile();
    member.setUserId(user.getId());
    member.setOrgId(org.getId());
    member.setDisplayName(request.getDisplayName().trim());
    member.setRealName(null);
    member.setJobTitle(request.getJobTitle().trim());
    member.setEducationLevel("UNKNOWN");
    member.setGraduatedSchool("待完善");
    member.setMajor(null);
    member.setYearsOfExperience(0);
    member.setSpecialCountries("待完善");
    member.setSpecialDirections("待完善");
    member.setBio("该成员尚未完善个人简介");
    member.setServiceStyleTags(null);
    member.setPublicStatus("PRIVATE");
    member.setVerifiedBadgeStatus(calcMemberBadgeStatus(user.getId()));
    member.setProfileAuditStatus("DRAFT");
    member.setStatus("ACTIVE");
    agencyMemberProfileMapper.insertOne(member);

    agencyMemberRoleRelMapper.deleteByMemberId(member.getId());
    agencyMemberRoleRelMapper.insertOne(member.getId(), mapJobTitleToRoleCode(member.getJobTitle()), true);
    replaceMemberPermissions(member.getId(), request.getPermissions());
    ensureDefaultPermissions(member.getId());

    AgencyDtos.MemberAdminItem created = findOrgMemberOrThrow(org.getId(), member.getId());
    created.setRoleCodes(agencyMemberRoleRelMapper.listRoleCodesByMemberId(member.getId()));
    created.setPermissions(agencyMemberPermissionRelMapper.listPermissionCodesByMemberId(member.getId()));
    return created;
  }

  @Override
  @Transactional
  public void updateOrgMember(Long userId, Long memberId, AgencyDtos.MemberProfileUpdateRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    AgencyMemberProfile member = requireOrgMember(org.getId(), memberId);
    member.setDisplayName(request.getDisplayName().trim());
    member.setJobTitle(request.getJobTitle().trim());
    member.setEducationLevel(request.getEducationLevel().trim());
    member.setGraduatedSchool(request.getGraduatedSchool().trim());
    member.setMajor(request.getMajor());
    member.setYearsOfExperience(request.getYearsOfExperience());
    member.setSpecialCountries(request.getSpecialCountries().trim());
    member.setSpecialDirections(request.getSpecialDirections().trim());
    member.setBio(request.getBio().trim());
    member.setServiceStyleTags(request.getServiceStyleTags());
    member.setPublicStatus(request.getPublicStatus().trim());
    agencyMemberProfileMapper.updateByIdForAdmin(member);
  }

  @Override
  @Transactional
  public void updateOrgMemberRoles(Long userId, Long memberId, AgencyDtos.MemberRolesUpdateRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    requireOrgMember(org.getId(), memberId);
    validateRoles(request.getRoles());
    replaceMemberRoles(memberId, request.getRoles());
  }

  @Override
  @Transactional
  public void updateOrgMemberStatus(Long userId, Long memberId, AgencyDtos.MemberStatusUpdateRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    AgencyMemberProfile member = requireOrgMember(org.getId(), memberId);
    userAccountMapper.updateStatus(member.getUserId(), request.getStatus().trim());
  }

  @Override
  @Transactional
  public void softDeleteOrgMember(Long userId, Long memberId) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    AgencyMemberProfile member = requireOrgMember(org.getId(), memberId);
    agencyMemberProfileMapper.updateStatusById(member.getId(), "DELETED");
    userAccountMapper.updateStatus(member.getUserId(), "DISABLED");
  }

  @Override
  @Transactional
  public void updateOrgMemberPermissions(Long userId, Long memberId, AgencyDtos.MemberPermissionsUpdateRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    requireOrgMember(org.getId(), memberId);
    replaceMemberPermissions(memberId, request.getPermissions());
    ensureDefaultPermissions(memberId);
  }

  @Override
  public AgencyDtos.MemberWorkbenchAccessView getMyWorkbenchAccess(Long userId) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }
    AgencyOrg org = agencyOrgMapper.findById(member.getOrgId());
    List<String> permissions = agencyMemberPermissionRelMapper.listPermissionCodesByMemberId(member.getId());
    LinkedHashSet<String> permissionSet = new LinkedHashSet<>(permissions);
    VerificationRecord cert = verificationRecordMapper.findOne(userId, "AGENT_MEMBER_CERT");
    String certStatus = cert == null ? "UNVERIFIED" : cert.getStatus();
    boolean orgApproved = org != null && "APPROVED".equals(org.getVerificationStatus());
    boolean memberApproved = "APPROVED".equals(certStatus);
    AgencyDtos.MemberWorkbenchAccessView view = new AgencyDtos.MemberWorkbenchAccessView();
    view.setOrgVerificationStatus(org == null ? "PENDING" : org.getVerificationStatus());
    view.setMemberVerificationStatus(certStatus);
    view.setPermissions(permissions);
    view.setCanChatStudent(orgApproved && memberApproved && permissionSet.contains("CAN_CHAT_STUDENT"));
    view.setCanPublishPackage(orgApproved && memberApproved && permissionSet.contains("CAN_PUBLISH_PACKAGE"));
    view.setCanDoCoreActions(orgApproved && memberApproved);
    if (!orgApproved) {
      view.setBlockedReason("当前机构尚未通过认证");
    } else if (!memberApproved) {
      view.setBlockedReason("员工认证未通过，暂不可执行核心操作");
    } else {
      view.setBlockedReason("");
    }
    return view;
  }

  @Override
  @Transactional
  public AgencyDtos.TeamView createTeam(Long userId, AgencyDtos.TeamCreateRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    AgencyTeam team = new AgencyTeam();
    team.setOrgId(org.getId());
    team.setTeamName(request.getTeamName().trim());
    team.setTeamType(request.getTeamType());
    team.setTeamIntro(request.getTeamIntro());
    team.setServiceCountryScope(request.getServiceCountryScope());
    team.setServiceMajorScope(request.getServiceMajorScope());
    agencyTeamMapper.insertOne(team);
    return toTeamView(team);
  }

  @Override
  public List<AgencyDtos.TeamView> listTeams(Long userId) {
    AgencyOrg org = resolveOrgForUser(userId);
    if (org == null) return List.of();
    return agencyTeamMapper.listByOrgId(org.getId()).stream().map(this::toTeamView).toList();
  }

  @Override
  @Transactional
  public AgencyDtos.InvitationView createInvitation(Long userId, AgencyDtos.InvitationCreateRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    requireOrgApproved(org);
    AgencyTeam team = agencyTeamMapper.findById(request.getTeamId());
    if (team == null || !org.getId().equals(team.getOrgId())) {
      throw new BizException("BIZ_BAD_REQUEST", "团队不存在或不属于当前机构");
    }

    AgencyInvitation invitation = new AgencyInvitation();
    invitation.setOrgId(org.getId());
    invitation.setTeamId(team.getId());
    invitation.setEmail(request.getEmail().trim());
    invitation.setInviteeName(request.getInviteeName());
    invitation.setRoleHint(request.getRoleHint());
    invitation.setToken(UUID.randomUUID().toString().replace("-", ""));
    invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
    invitation.setCreatedBy(userId);
    agencyInvitationMapper.insertOne(invitation);

    AgencyDtos.InvitationView view = new AgencyDtos.InvitationView();
    view.setId(invitation.getId());
    view.setEmail(invitation.getEmail());
    view.setStatus("PENDING");
    view.setToken(invitation.getToken());
    return view;
  }

  @Override
  @Transactional
  public void acceptInvitation(Long userId, String token) {
    UserAccount user = requireUser(userId);
    AgencyInvitation invitation = agencyInvitationMapper.findByToken(token);
    if (invitation == null) {
      throw new BizException("BIZ_NOT_FOUND", "邀请不存在");
    }
    if (!"PENDING".equals(invitation.getStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "邀请已失效");
    }
    if (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BizException("BIZ_BAD_REQUEST", "邀请已过期");
    }

    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      member = new AgencyMemberProfile();
      member.setUserId(userId);
      member.setOrgId(invitation.getOrgId());
      member.setDisplayName(defaultStr(invitation.getInviteeName(), "顾问" + userId));
      member.setRealName(null);
      member.setJobTitle(defaultStr(invitation.getRoleHint(), "咨询顾问"));
      member.setEducationLevel("UNKNOWN");
      member.setGraduatedSchool("待完善");
      member.setMajor(null);
      member.setYearsOfExperience(0);
      member.setSpecialCountries("待完善");
      member.setSpecialDirections("待完善");
      member.setBio("该成员尚未完善个人简介");
      member.setServiceStyleTags(null);
      member.setPublicStatus("PRIVATE");
      member.setVerifiedBadgeStatus("PENDING");
      member.setProfileAuditStatus("DRAFT");
      member.setStatus("ACTIVE");
      agencyMemberProfileMapper.insertOne(member);
    } else if (!invitation.getOrgId().equals(member.getOrgId())) {
      throw new BizException("BIZ_FORBIDDEN", "当前账号已归属其他机构");
    }

    Long existingTeamId = agencyTeamMemberRelMapper.findTeamIdByMemberId(member.getId());
    if (existingTeamId == null) {
      agencyTeamMemberRelMapper.insertOne(invitation.getTeamId(), member.getId());
    } else {
      agencyTeamMemberRelMapper.updateTeamByMemberId(invitation.getTeamId(), member.getId());
    }

    if (invitation.getRoleHint() != null && !invitation.getRoleHint().isBlank()) {
      agencyMemberRoleRelMapper.deleteByMemberId(member.getId());
      agencyMemberRoleRelMapper.insertOne(member.getId(), invitation.getRoleHint().trim(), true);
    }

    AgencyMemberMetrics metrics = agencyMemberMetricsMapper.findByMemberId(member.getId());
    if (metrics == null) {
      metrics = new AgencyMemberMetrics();
      metrics.setMemberId(member.getId());
      metrics.setCaseCount(0);
      metrics.setSuccessRate(java.math.BigDecimal.ZERO);
      metrics.setAvgRating(java.math.BigDecimal.ZERO);
      metrics.setResponseEfficiencyScore(java.math.BigDecimal.ZERO);
      metrics.setServiceTags(null);
      metrics.setBudgetTags(null);
      agencyMemberMetricsMapper.upsert(metrics);
    }

    if (!"AGENT_MEMBER".equals(user.getRole())) {
      userAccountMapper.updateRole(userId, "AGENT_MEMBER");
    }
    agencyInvitationMapper.markAccepted(invitation.getId(), userId);
  }

  @Override
  public AgencyDtos.MemberSelfProfileView getMyProfile(Long userId) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }
    return toMemberSelfView(member);
  }

  @Override
  public AgencyDtos.MemberVerificationStatusView getMyVerificationStatus(Long userId) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    VerificationRecord cert = verificationRecordMapper.findOne(userId, "AGENT_MEMBER_CERT");
    AgencyDtos.MemberVerificationStatusView view = new AgencyDtos.MemberVerificationStatusView();
    view.setStatus(cert == null ? "UNVERIFIED" : cert.getStatus());
    view.setRejectReason(cert == null ? null : cert.getRejectReason());
    view.setPayloadJson(cert == null ? null : cert.getPayloadJson());
    view.setSubmittedAt(cert == null || cert.getSubmittedAt() == null ? null : cert.getSubmittedAt().toString());
    return view;
  }

  @Override
  @Transactional
  public void updateMyProfile(Long userId, AgencyDtos.MemberProfileUpdateRequest request) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在，请先接受机构邀请");
    }
    member.setDisplayName(request.getDisplayName().trim());
    member.setJobTitle(request.getJobTitle().trim());
    member.setEducationLevel(request.getEducationLevel().trim());
    member.setGraduatedSchool(request.getGraduatedSchool().trim());
    member.setMajor(request.getMajor());
    member.setYearsOfExperience(request.getYearsOfExperience());
    member.setSpecialCountries(request.getSpecialCountries().trim());
    member.setSpecialDirections(request.getSpecialDirections().trim());
    member.setBio(request.getBio().trim());
    member.setServiceStyleTags(request.getServiceStyleTags());
    member.setPublicStatus(request.getPublicStatus().trim());
    member.setVerifiedBadgeStatus(calcMemberBadgeStatus(userId));
    member.setProfileAuditStatus("DRAFT");
    agencyMemberProfileMapper.updateByUserId(member);
  }

  @Override
  @Transactional
  public void updateMyAvatar(Long userId, AgencyDtos.MemberAvatarUpdateRequest request) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }
    agencyMemberProfileMapper.updateAvatarByUserId(userId, request.getAvatarUrl().trim());
  }

  @Override
  @Transactional
  public void submitMyVerification(Long userId, AgencyDtos.MemberVerificationSubmitRequest request) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }

    AgencyMemberVerificationMaterial material = new AgencyMemberVerificationMaterial();
    material.setUserId(userId);
    material.setIdCardImageUrl(request.getIdCardImageUrl().trim());
    material.setEmploymentProofImageUrl(request.getEmploymentProofImageUrl().trim());
    material.setEducationProofImageUrl(request.getEducationProofImageUrl().trim());
    agencyMemberVerificationMaterialMapper.upsert(material);

    String payloadJson;
    try {
      java.util.Map<String, String> payload = new java.util.LinkedHashMap<>();
      payload.put("idCardImageUrl", request.getIdCardImageUrl().trim());
      payload.put("employmentProofImageUrl", request.getEmploymentProofImageUrl().trim());
      payload.put("educationProofImageUrl", request.getEducationProofImageUrl().trim());
      payloadJson = objectMapper.writeValueAsString(payload);
    } catch (Exception ex) {
      throw new BizException("BIZ_INTERNAL_ERROR", "系统异常");
    }

    VerificationRecord cert = new VerificationRecord();
    cert.setUserId(userId);
    cert.setVerifyType("AGENT_MEMBER_CERT");
    cert.setStatus("PENDING");
    cert.setPayloadJson(payloadJson);
    cert.setSubmittedAt(LocalDateTime.now());
    verificationRecordMapper.upsert(cert);
    agencyMemberProfileMapper.updateProfileAuditStatusByUserId(userId, "PENDING");
  }

  @Override
  @Transactional
  public void submitMyProfileForAudit(Long userId) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }
    agencyMemberProfileMapper.updateProfileAuditStatusByUserId(userId, "PENDING");
  }

  @Override
  @Transactional
  public void updateMyRoles(Long userId, AgencyDtos.MemberRolesUpdateRequest request) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }

    long primaryCount = request.getRoles().stream().filter(r -> Boolean.TRUE.equals(r.getIsPrimary())).count();
    if (primaryCount != 1) {
      throw new BizException("BIZ_BAD_REQUEST", "必须且只能设置一个主角色");
    }

    agencyMemberRoleRelMapper.deleteByMemberId(member.getId());
    for (AgencyDtos.RoleItem item : request.getRoles()) {
      agencyMemberRoleRelMapper.insertOne(member.getId(), item.getRoleCode().trim(), Boolean.TRUE.equals(item.getIsPrimary()));
    }
  }

  @Override
  @Transactional
  public void updateMyMetrics(Long userId, AgencyDtos.MemberMetricsUpdateRequest request) {
    requireRole(requireUser(userId), "AGENT_MEMBER");
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }
    AgencyMemberMetrics metrics = new AgencyMemberMetrics();
    metrics.setMemberId(member.getId());
    metrics.setCaseCount(request.getCaseCount());
    metrics.setSuccessRate(request.getSuccessRate());
    metrics.setAvgRating(request.getAvgRating());
    metrics.setResponseEfficiencyScore(request.getResponseEfficiencyScore());
    metrics.setServiceTags(request.getServiceTags());
    metrics.setBudgetTags(request.getBudgetTags());
    agencyMemberMetricsMapper.upsert(metrics);
  }

  @Override
  public List<AgencyDtos.DiscoveryMemberItem> listDiscoveryMembers(String roleCode,
                                                                    String country,
                                                                    String direction,
                                                                    String city,
                                                                    String serviceTag,
                                                                    String budgetTag) {
    return agencyMemberProfileMapper.listDiscovery(roleCode, country, direction, city, serviceTag, budgetTag);
  }

  @Override
  public AgencyDtos.DiscoveryMemberDetail getDiscoveryMemberDetail(Long memberId) {
    AgencyDtos.DiscoveryMemberItem item = agencyMemberProfileMapper.findDiscoveryByMemberId(memberId);
    if (item == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员不存在或不可展示");
    }
    AgencyMemberProfile member = agencyMemberProfileMapper.findById(memberId);
    AgencyOrg org = agencyOrgMapper.findById(member.getOrgId());

    AgencyDtos.DiscoveryMemberDetail detail = new AgencyDtos.DiscoveryMemberDetail();
    detail.setMember(item);
    detail.setOrg(toOrgView(org));
    detail.setRoleCodes(agencyMemberRoleRelMapper.listRoleCodesByMemberId(memberId));
    return detail;
  }

  @Override
  public List<AgencyDtos.DiscoveryTeamItem> listDiscoveryTeams(String keyword,
                                                               String country,
                                                               String direction,
                                                               String city,
                                                               String roleCode,
                                                               String serviceTag) {
    return agencyTeamMapper.listDiscoveryTeams(keyword, country, direction, city, roleCode, serviceTag);
  }

  @Override
  public AgencyDtos.DiscoveryTeamDetail getDiscoveryTeamDetail(Long teamId) {
    AgencyDtos.DiscoveryTeamDetail detail = agencyTeamMapper.findDiscoveryTeamDetail(teamId);
    if (detail == null) {
      throw new BizException("BIZ_NOT_FOUND", "团队不存在或不可展示");
    }

    AgencyTeam team = agencyTeamMapper.findById(teamId);
    if (team != null) {
      AgencyOrg org = agencyOrgMapper.findById(team.getOrgId());
      if (org != null) {
        detail.setOrg(toOrgView(org));
      }
    }
    detail.setMembers(agencyTeamMapper.listDiscoveryTeamMembers(teamId));
    return detail;
  }

  private AgencyDtos.OrgVerificationView upsertOrgVerification(Long userId, AgencyDtos.OrgVerificationSubmitRequest request) {
    AgencyOrg org = requireOrgByAdmin(userId);
    String payloadJson;
    try {
      Map<String, Object> payload = new HashMap<>();
      payload.put("licenseNo", request.getLicenseNo().trim());
      payload.put("legalPersonName", request.getLegalPersonName().trim());
      payload.put("licenseImageUrl", request.getLicenseImageUrl().trim());
      payload.put("legalPersonIdImageUrl", request.getLegalPersonIdImageUrl().trim());
      payload.put("corporateAccountName", request.getCorporateAccountName().trim());
      payload.put("corporateBankName", request.getCorporateBankName().trim());
      payload.put("corporateBankAccountNo", request.getCorporateBankAccountNo().trim());
      payload.put("corporateAccountProofImageUrl", request.getCorporateAccountProofImageUrl());
      payload.put("officeEnvironmentImageUrls", request.getOfficeEnvironmentImageUrls());
      payload.put("adminRealNameImageUrl", request.getAdminRealNameImageUrl().trim());
      payload.put("adminEmploymentProofImageUrl", request.getAdminEmploymentProofImageUrl().trim());
      payload.put("remark", request.getRemark());
      payloadJson = objectMapper.writeValueAsString(payload);
    } catch (Exception ex) {
      throw new BizException("BIZ_BAD_REQUEST", "认证材料格式不合法");
    }

    VerificationRecord record = new VerificationRecord();
    record.setUserId(userId);
    record.setVerifyType("AGENT_ORG");
    record.setStatus("PENDING");
    record.setPayloadJson(payloadJson);
    record.setSubmittedAt(LocalDateTime.now());
    verificationRecordMapper.upsert(record);
    agencyOrgMapper.updateVerificationStatus(org.getId(), "PENDING");
    AgencyOrg freshOrg = agencyOrgMapper.findById(org.getId());
    VerificationRecord freshRecord = verificationRecordMapper.findOne(userId, "AGENT_ORG");
    return toOrgVerificationView(freshOrg, freshRecord);
  }

  private AgencyDtos.OrgVerificationView toOrgVerificationView(AgencyOrg org, VerificationRecord record) {
    AgencyDtos.OrgVerificationView view = new AgencyDtos.OrgVerificationView();
    view.setVerificationStatus(org.getVerificationStatus());
    if (record != null) {
      view.setRecordStatus(record.getStatus());
      view.setPayloadJson(record.getPayloadJson());
      view.setRejectReason(record.getRejectReason());
      view.setSubmittedAt(record.getSubmittedAt() == null ? null : record.getSubmittedAt().toString());
    }
    return view;
  }

  private AgencyMemberProfile requireOrgMember(Long orgId, Long memberId) {
    AgencyMemberProfile member = agencyMemberProfileMapper.findByOrgAndMemberId(orgId, memberId);
    if (member == null) {
      throw new BizException("BIZ_NOT_FOUND", "成员不存在或不属于当前机构");
    }
    return member;
  }

  private AgencyDtos.MemberAdminItem findOrgMemberOrThrow(Long orgId, Long memberId) {
    return agencyMemberProfileMapper.listByOrgId(orgId).stream()
      .filter(item -> memberId.equals(item.getMemberId()))
      .findFirst()
      .orElseThrow(() -> new BizException("BIZ_NOT_FOUND", "成员不存在"));
  }

  private void validateRoles(List<AgencyDtos.RoleItem> roles) {
    long primaryCount = roles.stream().filter(r -> Boolean.TRUE.equals(r.getIsPrimary())).count();
    if (primaryCount != 1) {
      throw new BizException("BIZ_BAD_REQUEST", "必须且只能设置一个主角色");
    }
  }

  private void replaceMemberRoles(Long memberId, List<AgencyDtos.RoleItem> roles) {
    agencyMemberRoleRelMapper.deleteByMemberId(memberId);
    for (AgencyDtos.RoleItem role : roles) {
      agencyMemberRoleRelMapper.insertOne(memberId, role.getRoleCode().trim(), Boolean.TRUE.equals(role.getIsPrimary()));
    }
  }

  private void replaceMemberPermissions(Long memberId, List<String> permissions) {
    agencyMemberPermissionRelMapper.deleteByMemberId(memberId);
    if (permissions == null) return;
    LinkedHashSet<String> dedup = new LinkedHashSet<>();
    for (String permission : permissions) {
      if (permission == null || permission.isBlank()) continue;
      dedup.add(permission.trim());
    }
    for (String permissionCode : dedup) {
      agencyMemberPermissionRelMapper.insertOne(memberId, permissionCode);
    }
  }

  private void ensureDefaultPermissions(Long memberId) {
    List<String> permissions = agencyMemberPermissionRelMapper.listPermissionCodesByMemberId(memberId);
    if (permissions.contains("CAN_CHAT_STUDENT")) return;
    agencyMemberPermissionRelMapper.insertOne(memberId, "CAN_CHAT_STUDENT");
  }

  private String mapJobTitleToRoleCode(String jobTitle) {
    if (jobTitle == null) return "CONSULTANT";
    return switch (jobTitle.trim()) {
      case "咨询顾问" -> "CONSULTANT";
      case "文书顾问" -> "WRITER";
      case "规划顾问" -> "PLANNER";
      case "签证专员" -> "VISA_SPECIALIST";
      case "申请专员" -> "APPLY_SPECIALIST";
      case "后续服务" -> "AFTERCARE";
      default -> "CONSULTANT";
    };
  }

  private boolean contains(String source, String keyword) {
    return source != null && source.contains(keyword);
  }

  private AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem> toPaged(List<AgencyDtos.MemberAdminItem> all, int page, int pageSize) {
    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 200));
    int from = (safePage - 1) * safePageSize;
    int to = Math.min(from + safePageSize, all.size());
    List<AgencyDtos.MemberAdminItem> records = from >= all.size() ? List.of() : all.subList(from, to);

    AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem> result = new AgencyDtos.PagedResult<>();
    result.setRecords(records);
    result.setTotal(all.size());
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    return result;
  }

  private String calcMemberBadgeStatus(Long userId) {
    VerificationRecord real = verificationRecordMapper.findOne(userId, "AGENT_REAL_NAME");
    VerificationRecord edu = verificationRecordMapper.findOne(userId, "AGENT_EDUCATION");
    if (real != null && edu != null && "APPROVED".equals(real.getStatus()) && "APPROVED".equals(edu.getStatus())) {
      return "APPROVED";
    }
    return "PENDING";
  }

  private AgencyOrg requireOrgByAdmin(Long userId) {
    requireRole(requireUser(userId), "AGENT_ORG");
    AgencyOrg org = agencyOrgMapper.findByAdminUserId(userId);
    if (org == null) {
      throw new BizException("BIZ_NOT_FOUND", "机构档案不存在，请先创建机构信息");
    }
    return org;
  }

  private void requireOrgApproved(AgencyOrg org) {
    if (!"APPROVED".equals(org.getVerificationStatus())) {
      throw new BizException("BIZ_FORBIDDEN", "机构认证未通过，暂不可执行该操作");
    }
  }

  private AgencyOrg resolveOrgForUser(Long userId) {
    UserAccount user = requireUser(userId);
    if ("AGENT_ORG".equals(user.getRole())) {
      return agencyOrgMapper.findByAdminUserId(userId);
    }
    if ("AGENT_MEMBER".equals(user.getRole())) {
      return agencyOrgMapper.findByMemberUserId(userId);
    }
    return null;
  }

  private UserAccount requireUser(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "用户不存在");
    }
    if (!"ACTIVE".equals(user.getStatus())) {
      throw new BizException("BIZ_ACCOUNT_DISABLED", "账号不可用");
    }
    return user;
  }

  private void requireRole(UserAccount user, String role) {
    if (!role.equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "无权限执行该操作");
    }
  }

  private AgencyOrg buildOrgEntity(Long userId, AgencyDtos.OrgProfileUpsertRequest request) {
    AgencyOrg entity = new AgencyOrg();
    entity.setAdminUserId(userId);
    entity.setOrgName(request.getOrgName().trim());
    entity.setBrandName(request.getBrandName());
    entity.setCountryCode(request.getCountryCode().trim());
    entity.setProvinceOrState(request.getProvinceOrState().trim());
    entity.setCity(request.getCity().trim());
    entity.setDistrict(request.getDistrict());
    entity.setAddressLine(request.getAddressLine().trim());
    entity.setLogoUrl(request.getLogoUrl());
    entity.setCoverImageUrl(request.getCoverImageUrl());
    entity.setOfficeEnvironmentImages(request.getOfficeEnvironmentImages());
    entity.setContactPhone(request.getContactPhone().trim());
    entity.setContactEmail(request.getContactEmail());
    entity.setWebsiteUrl(request.getWebsiteUrl());
    entity.setSocialLinks(request.getSocialLinks());
    entity.setFoundedYear(request.getFoundedYear());
    entity.setTeamSizeRange(request.getTeamSizeRange());
    entity.setServiceMode(request.getServiceMode().trim());
    entity.setOrgIntro(request.getOrgIntro().trim());
    entity.setOrgSlogan(request.getOrgSlogan());
    return entity;
  }

  private AgencyDtos.OrgProfileView toOrgView(AgencyOrg org) {
    AgencyDtos.OrgProfileView view = new AgencyDtos.OrgProfileView();
    view.setId(org.getId());
    view.setOrgName(org.getOrgName());
    view.setBrandName(org.getBrandName());
    view.setCountryCode(org.getCountryCode());
    view.setProvinceOrState(org.getProvinceOrState());
    view.setCity(org.getCity());
    view.setDistrict(org.getDistrict());
    view.setAddressLine(org.getAddressLine());
    view.setLogoUrl(org.getLogoUrl());
    view.setCoverImageUrl(org.getCoverImageUrl());
    view.setOfficeEnvironmentImages(org.getOfficeEnvironmentImages());
    view.setContactPhone(org.getContactPhone());
    view.setContactEmail(org.getContactEmail());
    view.setWebsiteUrl(org.getWebsiteUrl());
    view.setSocialLinks(org.getSocialLinks());
    view.setFoundedYear(org.getFoundedYear());
    view.setTeamSizeRange(org.getTeamSizeRange());
    view.setServiceMode(org.getServiceMode());
    view.setOrgIntro(org.getOrgIntro());
    view.setOrgSlogan(org.getOrgSlogan());
    view.setVerificationStatus(org.getVerificationStatus());
    return view;
  }

  private AgencyDtos.TeamView toTeamView(AgencyTeam team) {
    AgencyDtos.TeamView view = new AgencyDtos.TeamView();
    view.setId(team.getId());
    view.setTeamName(team.getTeamName());
    view.setTeamType(team.getTeamType());
    view.setTeamIntro(team.getTeamIntro());
    view.setServiceCountryScope(team.getServiceCountryScope());
    view.setServiceMajorScope(team.getServiceMajorScope());
    return view;
  }

  private AgencyDtos.MemberSelfProfileView toMemberSelfView(AgencyMemberProfile member) {
    AgencyDtos.MemberSelfProfileView view = new AgencyDtos.MemberSelfProfileView();
    view.setMemberId(member.getId());
    view.setOrgId(member.getOrgId());
    view.setDisplayName(member.getDisplayName());
    view.setAvatarUrl(member.getAvatarUrl());
    view.setJobTitle(member.getJobTitle());
    view.setProfileAuditStatus(member.getProfileAuditStatus());
    return view;
  }

  private String defaultStr(String input, String fallback) {
    return (input == null || input.isBlank()) ? fallback : input.trim();
  }

  private void insertUserCompat(String phone, String role) {
    try {
      userAccountMapper.insertByRole(phone, role);
    } catch (Exception ex) {
      String message = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
      if (message.contains("password_hash") || message.contains("doesn't have a default value") || message.contains("cannot be null")) {
        userAccountMapper.insertByRoleWithEmptyPassword(phone, role);
        return;
      }
      throw ex;
    }
  }
}
