package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AiSchoolRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiSchoolRecommendationMapper {
  int insertOne(AiSchoolRecommendation recommendation);
  List<AiSchoolRecommendation> listByReportId(@Param("reportId") Long reportId);
}
