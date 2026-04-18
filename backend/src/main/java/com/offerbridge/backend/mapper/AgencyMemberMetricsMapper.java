package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AgencyMemberMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgencyMemberMetricsMapper {
  AgencyMemberMetrics findByMemberId(@Param("memberId") Long memberId);
  int upsert(AgencyMemberMetrics entity);
}
