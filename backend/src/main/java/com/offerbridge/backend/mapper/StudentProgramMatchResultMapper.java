package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.StudentProgramMatchResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentProgramMatchResultMapper {
  int upsert(@Param("userId") Long userId,
             @Param("programId") Long programId,
             @Param("matchScore") Integer matchScore,
             @Param("matchTier") String matchTier,
             @Param("reasonTagsJson") String reasonTagsJson);

  StudentProgramMatchResult findOne(@Param("userId") Long userId,
                                    @Param("programId") Long programId);

  List<StudentProgramMatchResult> listByUserId(@Param("userId") Long userId);
}
