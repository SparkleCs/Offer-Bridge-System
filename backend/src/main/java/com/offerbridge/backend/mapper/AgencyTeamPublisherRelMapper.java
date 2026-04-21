package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgencyTeamPublisherRelMapper {
  int upsert(@Param("teamId") Long teamId,
             @Param("memberId") Long memberId,
             @Param("roleCode") String roleCode);

  int deactivateByTeamId(@Param("teamId") Long teamId);

  List<Long> listActiveMemberIds(@Param("teamId") Long teamId);

  int countActivePublisher(@Param("teamId") Long teamId,
                           @Param("memberId") Long memberId,
                           @Param("roleCode") String roleCode);
}
