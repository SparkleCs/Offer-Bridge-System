package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgencyTeamMemberRelMapper {
  Long findTeamIdByMemberId(@Param("memberId") Long memberId);
  int insertOne(@Param("teamId") Long teamId, @Param("memberId") Long memberId);
  int updateTeamByMemberId(@Param("teamId") Long teamId, @Param("memberId") Long memberId);
}
