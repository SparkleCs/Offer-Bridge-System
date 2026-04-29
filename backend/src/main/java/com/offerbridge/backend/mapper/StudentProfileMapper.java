package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentProfileMapper {
  StudentProfile findByUserId(@Param("userId") Long userId);
  List<StudentProfile> listByUserIds(@Param("userIds") List<Long> userIds);
  List<AgencyDtos.AgentStudentSearchItem> searchAgentStudents(@Param("keyword") String keyword,
                                                              @Param("country") String country,
                                                              @Param("educationLevel") String educationLevel,
                                                              @Param("scoreBucket") String scoreBucket,
                                                              @Param("subjectCategoryCode") String subjectCategoryCode);
  int insertEmpty(@Param("userId") Long userId);
  int updateProfile(StudentProfile profile);
  int updateWechatId(@Param("userId") Long userId, @Param("wechatId") String wechatId);
}
