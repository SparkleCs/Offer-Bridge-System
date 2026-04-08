package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.VerificationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VerificationRecordMapper {
  VerificationRecord findOne(@Param("userId") Long userId, @Param("verifyType") String verifyType);
  int upsert(VerificationRecord record);
}
