package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentProfileMapper {
  StudentProfile findByUserId(@Param("userId") Long userId);
  List<StudentProfile> listByUserIds(@Param("userIds") List<Long> userIds);
  int insertEmpty(@Param("userId") Long userId);
  int updateProfile(StudentProfile profile);
}
