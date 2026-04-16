package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.UniversityDtos;

import java.util.List;

public interface UniversityService {
  UniversityDtos.MetaView getMeta();
  List<UniversityDtos.SchoolListItem> listSchools(String countryCode,
                                                  String subjectCategoryCode,
                                                  String directionCode,
                                                  Integer rankMin,
                                                  Integer rankMax,
                                                  String keyword);
  UniversityDtos.SchoolDetailView getSchoolDetail(Long schoolId);
  List<UniversityDtos.ProgramListItem> listPrograms(Long schoolId,
                                                    String countryCode,
                                                    String subjectCategoryCode,
                                                    String directionCode,
                                                    String keyword);
  UniversityDtos.ProgramDetailView getProgramDetail(Long userId, Long programId);

  UniversityDtos.ApplicationListView getApplicationList(Long userId);
  UniversityDtos.ApplicationListView addApplication(Long userId, UniversityDtos.AddApplicationRequest request);
  UniversityDtos.ApplicationListView updateApplicationStatus(Long userId, Long applicationId, String statusCode);
  UniversityDtos.ApplicationListView removeApplication(Long userId, Long applicationId);
}
