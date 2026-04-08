package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentWorkExperience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentWorkExperienceMapper {
  List<StudentWorkExperience> listByUserId(@Param("userId") Long userId);
  int deleteByUserId(@Param("userId") Long userId);
  int insertOne(StudentWorkExperience item);
}
