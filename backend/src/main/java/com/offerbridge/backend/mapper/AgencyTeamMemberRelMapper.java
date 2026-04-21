package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgencyTeamMemberRelMapper {
  int insertOne(@Param("teamId") Long teamId, @Param("memberId") Long memberId);
  int activateRelation(@Param("teamId") Long teamId, @Param("memberId") Long memberId);
}
