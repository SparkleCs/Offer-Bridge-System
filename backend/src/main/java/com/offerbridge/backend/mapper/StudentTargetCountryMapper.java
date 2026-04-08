package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentTargetCountry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentTargetCountryMapper {
  List<StudentTargetCountry> listByUserId(@Param("userId") Long userId);
  int deleteByUserId(@Param("userId") Long userId);
  int insertOne(@Param("userId") Long userId, @Param("countryName") String countryName);
}
