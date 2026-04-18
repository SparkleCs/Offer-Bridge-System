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
  int insertOne(AgencyMemberProfile entity);
  int updateByUserId(AgencyMemberProfile entity);

  List<AgencyDtos.DiscoveryMemberItem> listDiscovery(@Param("roleCode") String roleCode,
                                                      @Param("country") String country,
                                                      @Param("direction") String direction,
                                                      @Param("city") String city,
                                                      @Param("serviceTag") String serviceTag,
                                                      @Param("budgetTag") String budgetTag);

  AgencyDtos.DiscoveryMemberItem findDiscoveryByMemberId(@Param("memberId") Long memberId);
}
