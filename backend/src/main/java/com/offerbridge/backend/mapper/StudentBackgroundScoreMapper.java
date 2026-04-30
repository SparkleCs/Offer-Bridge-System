package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentBackgroundScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentBackgroundScoreMapper {
  StudentBackgroundScore findByUserId(@Param("userId") Long userId);
  int upsert(StudentBackgroundScore score);
}
