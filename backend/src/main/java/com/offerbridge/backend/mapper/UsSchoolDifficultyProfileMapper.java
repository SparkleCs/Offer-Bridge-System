package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.UsSchoolDifficultyProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UsSchoolDifficultyProfileMapper {
  List<UsSchoolDifficultyProfile> listAll();
  UsSchoolDifficultyProfile findBySchoolId(@Param("schoolId") Long schoolId);
}
