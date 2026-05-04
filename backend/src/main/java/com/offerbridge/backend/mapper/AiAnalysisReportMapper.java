package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AiAnalysisReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiAnalysisReportMapper {
  int insertOne(AiAnalysisReport report);
  AiAnalysisReport findLatestByUserId(@Param("userId") Long userId, @Param("reportType") String reportType);
}
