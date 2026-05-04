package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AiProgramRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiProgramRecommendationMapper {
  int insertOne(AiProgramRecommendation recommendation);
  List<AiProgramRecommendation> listByReportId(@Param("reportId") Long reportId);
}
