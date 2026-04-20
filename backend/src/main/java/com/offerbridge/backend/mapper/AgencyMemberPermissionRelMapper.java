package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgencyMemberPermissionRelMapper {
  int deleteByMemberId(@Param("memberId") Long memberId);
  int insertOne(@Param("memberId") Long memberId, @Param("permissionCode") String permissionCode);
  List<String> listPermissionCodesByMemberId(@Param("memberId") Long memberId);
}
