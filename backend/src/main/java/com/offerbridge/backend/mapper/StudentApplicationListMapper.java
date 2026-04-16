package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentApplicationProgramRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentApplicationListMapper {
  int upsert(@Param("userId") Long userId,
             @Param("programId") Long programId,
             @Param("statusCode") String statusCode,
             @Param("noteText") String noteText);

  List<StudentApplicationProgramRow> listByUserId(@Param("userId") Long userId);

  int updateStatus(@Param("userId") Long userId,
                   @Param("applicationId") Long applicationId,
                   @Param("statusCode") String statusCode);

  int deleteById(@Param("userId") Long userId,
                 @Param("applicationId") Long applicationId);
}
