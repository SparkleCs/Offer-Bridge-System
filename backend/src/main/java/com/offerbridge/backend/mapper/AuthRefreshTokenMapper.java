package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AuthRefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AuthRefreshTokenMapper {
  int insertToken(@Param("userId") Long userId,
                  @Param("tokenHash") String tokenHash,
                  @Param("deviceInfo") String deviceInfo,
                  @Param("ip") String ip,
                  @Param("userAgent") String userAgent,
                  @Param("expiresAt") LocalDateTime expiresAt);

  AuthRefreshToken findValidToken(@Param("tokenHash") String tokenHash);

  int revokeToken(@Param("tokenHash") String tokenHash);
}
