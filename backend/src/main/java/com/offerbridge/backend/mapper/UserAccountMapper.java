package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAccountMapper {
  UserAccount findByPhone(@Param("phone") String phone);
  UserAccount findById(@Param("id") Long id);
  int insertByRole(@Param("phone") String phone, @Param("role") String role);
  int insertByRoleWithEmptyPassword(@Param("phone") String phone, @Param("role") String role);
  int updateLastLoginAt(@Param("id") Long id);
  int updateRole(@Param("id") Long id, @Param("role") String role);
}
