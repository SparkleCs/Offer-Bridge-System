package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentVerificationMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentVerificationMaterialMapper {
  StudentVerificationMaterial findByUserId(@Param("userId") Long userId);
  int upsert(StudentVerificationMaterial material);
}
