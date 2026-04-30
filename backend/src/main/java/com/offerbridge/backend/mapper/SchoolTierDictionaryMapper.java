package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.SchoolTierDictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SchoolTierDictionaryMapper {
  SchoolTierDictionary findBySchoolName(@Param("schoolName") String schoolName);
}
