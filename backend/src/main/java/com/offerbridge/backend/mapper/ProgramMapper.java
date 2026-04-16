package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.MajorDirection;
import com.offerbridge.backend.entity.Program;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProgramMapper {
  List<Program> listPrograms(@Param("schoolId") Long schoolId,
                             @Param("countryCode") String countryCode,
                             @Param("subjectCategoryCode") String subjectCategoryCode,
                             @Param("directionCode") String directionCode,
                             @Param("keyword") String keyword);

  List<Program> listProgramsBySchoolId(@Param("schoolId") Long schoolId,
                                       @Param("limit") Integer limit);

  Program findById(@Param("programId") Long programId);

  List<MajorDirection> listMajorDirections();
}
