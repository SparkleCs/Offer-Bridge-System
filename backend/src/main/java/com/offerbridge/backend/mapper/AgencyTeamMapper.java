package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.entity.AgencyTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgencyTeamMapper {
  int insertOne(AgencyTeam entity);
  int updateTeamProductById(AgencyTeam entity);
  int publishTeamProduct(@Param("id") Long id, @Param("orgId") Long orgId, @Param("publishedBy") Long publishedBy);
  AgencyTeam findById(@Param("id") Long id);
  List<AgencyTeam> listByOrgId(@Param("orgId") Long orgId);
  List<AgencyDtos.TeamProductSummaryItem> listTeamProductsByOrgId(@Param("orgId") Long orgId);
  AgencyDtos.TeamProductDetailView findTeamProductDetail(@Param("id") Long id, @Param("orgId") Long orgId);
  List<AgencyDtos.DiscoveryTeamItem> listDiscoveryTeams(@Param("keyword") String keyword,
                                                         @Param("country") String country,
                                                         @Param("direction") String direction,
                                                         @Param("city") String city,
                                                         @Param("roleCode") String roleCode,
                                                         @Param("serviceTag") String serviceTag);
  AgencyDtos.DiscoveryTeamDetail findDiscoveryTeamDetail(@Param("teamId") Long teamId);
  List<AgencyDtos.DiscoveryTeamMemberItem> listDiscoveryTeamMembers(@Param("teamId") Long teamId);
  List<AgencyDtos.TeamProductPublisherItem> listActivePublisherMembers(@Param("teamId") Long teamId);
}
