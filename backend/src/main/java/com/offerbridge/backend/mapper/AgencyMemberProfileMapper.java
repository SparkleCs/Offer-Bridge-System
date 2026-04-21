package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.entity.AgencyMemberProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgencyMemberProfileMapper {
  AgencyMemberProfile findByUserId(@Param("userId") Long userId);
  AgencyMemberProfile findById(@Param("id") Long id);
  AgencyMemberProfile findByOrgAndUserId(@Param("orgId") Long orgId, @Param("userId") Long userId);
  AgencyMemberProfile findByOrgAndMemberId(@Param("orgId") Long orgId, @Param("memberId") Long memberId);
  List<AgencyDtos.MemberAdminItem> listByOrgId(@Param("orgId") Long orgId);
  List<AgencyDtos.TeamProductOrgMemberItem> listOrgProductMembers(@Param("orgId") Long orgId, @Param("keyword") String keyword);
  int insertOne(AgencyMemberProfile entity);
  int updateByUserId(AgencyMemberProfile entity);
  int updateByIdForAdmin(AgencyMemberProfile entity);
  int updateStatusById(@Param("id") Long id, @Param("status") String status);
  int updateProfileAuditStatusByUserId(@Param("userId") Long userId, @Param("profileAuditStatus") String profileAuditStatus);
  int updateAvatarByUserId(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);

  List<AgencyDtos.DiscoveryMemberItem> listDiscovery(@Param("roleCode") String roleCode,
                                                      @Param("country") String country,
                                                      @Param("direction") String direction,
                                                      @Param("city") String city,
                                                      @Param("serviceTag") String serviceTag,
                                                      @Param("budgetTag") String budgetTag);

  AgencyDtos.DiscoveryMemberItem findDiscoveryByMemberId(@Param("memberId") Long memberId);
}
