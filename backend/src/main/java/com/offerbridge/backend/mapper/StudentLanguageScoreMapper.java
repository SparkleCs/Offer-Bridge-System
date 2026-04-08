package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentLanguageScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface StudentLanguageScoreMapper {
  List<StudentLanguageScore> listByUserId(@Param("userId") Long userId);
  int deleteByUserId(@Param("userId") Long userId);
  int insertOne(@Param("userId") Long userId, @Param("languageType") String languageType, @Param("score") BigDecimal score);
}
