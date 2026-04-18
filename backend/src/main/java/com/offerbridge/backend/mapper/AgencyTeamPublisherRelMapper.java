package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgencyTeamPublisherRelMapper {
  int upsert(@Param("teamId") Long teamId,
             @Param("memberId") Long memberId,
             @Param("roleCode") String roleCode);

  int countActivePublisher(@Param("teamId") Long teamId,
                           @Param("memberId") Long memberId,
                           @Param("roleCode") String roleCode);
}
