package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ServiceCaseMapper {
  int insertOne(@Param("orderId") Long orderId,
                @Param("studentUserId") Long studentUserId,
                @Param("orgId") Long orgId,
                @Param("teamId") Long teamId,
                @Param("assignedMemberId") Long assignedMemberId);
  Long findIdByOrderId(@Param("orderId") Long orderId);
}
