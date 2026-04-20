package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AgencyMemberVerificationMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgencyMemberVerificationMaterialMapper {
  AgencyMemberVerificationMaterial findByUserId(@Param("userId") Long userId);
  int upsert(AgencyMemberVerificationMaterial entity);
}

