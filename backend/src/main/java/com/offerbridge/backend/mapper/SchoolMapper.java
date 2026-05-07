package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.School;
import com.offerbridge.backend.entity.SubjectCategory;
import com.offerbridge.backend.entity.CountryOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SchoolMapper {
  List<School> listSchools(@Param("countryCode") String countryCode,
                           @Param("subjectCategoryCode") String subjectCategoryCode,
                           @Param("directionCode") String directionCode,
                           @Param("rankMin") Integer rankMin,
                           @Param("rankMax") Integer rankMax,
                           @Param("rankingSource") String rankingSource,
                           @Param("keyword") String keyword);

  School findById(@Param("schoolId") Long schoolId);

  List<SubjectCategory> listSubjectCategories();

  List<CountryOption> listCountries();
}
