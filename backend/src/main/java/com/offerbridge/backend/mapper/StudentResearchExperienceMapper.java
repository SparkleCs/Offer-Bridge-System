package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentResearchExperience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentResearchExperienceMapper {
  List<StudentResearchExperience> listByUserId(@Param("userId") Long userId);
  int deleteByUserId(@Param("userId") Long userId);
  int insertOne(StudentResearchExperience item);
}
