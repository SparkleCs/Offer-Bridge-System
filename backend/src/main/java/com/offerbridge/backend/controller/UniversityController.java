package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.UniversityDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.UniversityService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/universities")
@Validated
public class UniversityController {
  private final UniversityService universityService;

  public UniversityController(UniversityService universityService) {
    this.universityService = universityService;
  }

  @GetMapping("/meta")
  public ApiResponse<UniversityDtos.MetaView> getMeta() {
    return ApiResponse.ok(universityService.getMeta());
  }

  @GetMapping("/schools")
  public ApiResponse<List<UniversityDtos.SchoolListItem>> listSchools(
    @RequestParam(required = false) String countryCode,
    @RequestParam(required = false) String subjectCategoryCode,
    @RequestParam(required = false) String directionCode,
    @RequestParam(required = false) Integer rankMin,
    @RequestParam(required = false) Integer rankMax,
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(
      universityService.listSchools(countryCode, subjectCategoryCode, directionCode, rankMin, rankMax, keyword)
    );
  }

  @GetMapping("/schools/{schoolId}")
  public ApiResponse<UniversityDtos.SchoolDetailView> getSchoolDetail(@PathVariable Long schoolId) {
    return ApiResponse.ok(universityService.getSchoolDetail(schoolId));
  }

  @GetMapping("/programs")
  public ApiResponse<List<UniversityDtos.ProgramListItem>> listPrograms(
    @RequestParam(required = false) Long schoolId,
    @RequestParam(required = false) String countryCode,
    @RequestParam(required = false) String subjectCategoryCode,
    @RequestParam(required = false) String directionCode,
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(
      universityService.listPrograms(schoolId, countryCode, subjectCategoryCode, directionCode, keyword)
    );
  }

  @GetMapping("/programs/{programId}")
  public ApiResponse<UniversityDtos.ProgramDetailView> getProgramDetail(@PathVariable Long programId) {
    return ApiResponse.ok(universityService.getProgramDetail(AuthContext.getUserId(), programId));
  }
}
