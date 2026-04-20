package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AgencyOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgencyOrgMapper {
  AgencyOrg findByAdminUserId(@Param("adminUserId") Long adminUserId);
  AgencyOrg findById(@Param("id") Long id);
  AgencyOrg findByMemberUserId(@Param("userId") Long userId);
  int insertOne(AgencyOrg entity);
  int updateOne(AgencyOrg entity);
  int updateVerificationStatus(@Param("id") Long id, @Param("verificationStatus") String verificationStatus);
}
