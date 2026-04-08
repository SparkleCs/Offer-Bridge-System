package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginAuditLogMapper {
  int insertLog(@Param("userId") Long userId,
                @Param("phone") String phone,
                @Param("loginMethod") String loginMethod,
                @Param("success") boolean success,
                @Param("reason") String reason,
                @Param("ip") String ip,
                @Param("userAgent") String userAgent);
}
