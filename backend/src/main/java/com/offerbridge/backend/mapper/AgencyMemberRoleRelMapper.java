package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgencyMemberRoleRelMapper {
  int deleteByMemberId(@Param("memberId") Long memberId);
  int insertOne(@Param("memberId") Long memberId, @Param("roleCode") String roleCode, @Param("isPrimary") boolean isPrimary);
  List<String> listRoleCodesByMemberId(@Param("memberId") Long memberId);
}
