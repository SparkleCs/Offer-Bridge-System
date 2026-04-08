package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentExchangeExperience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentExchangeExperienceMapper {
  StudentExchangeExperience findByUserId(@Param("userId") Long userId);
  int upsert(StudentExchangeExperience item);
}
