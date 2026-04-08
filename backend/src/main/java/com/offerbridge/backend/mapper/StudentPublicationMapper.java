package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentPublication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentPublicationMapper {
  List<StudentPublication> listByUserId(@Param("userId") Long userId);
  int deleteByUserId(@Param("userId") Long userId);
  int insertOne(StudentPublication item);
}
