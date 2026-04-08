package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentCompetitionExperience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentCompetitionExperienceMapper {
  List<StudentCompetitionExperience> listByUserId(@Param("userId") Long userId);
  int deleteByUserId(@Param("userId") Long userId);
  int insertOne(StudentCompetitionExperience item);
}
