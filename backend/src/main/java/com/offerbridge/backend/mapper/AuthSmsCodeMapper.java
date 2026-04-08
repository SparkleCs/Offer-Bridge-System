package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AuthSmsCodeMapper {
  int insertCode(@Param("phone") String phone, @Param("scene") String scene, @Param("codeHash") String codeHash, @Param("expiresAt") LocalDateTime expiresAt);
  int countAnyValidCode(@Param("phone") String phone, @Param("scene") String scene);
  int countValidCode(@Param("phone") String phone, @Param("scene") String scene, @Param("codeHash") String codeHash);
  int markUsedByCode(@Param("phone") String phone, @Param("scene") String scene, @Param("codeHash") String codeHash);
  int increaseAttempt(@Param("phone") String phone, @Param("scene") String scene);
}
