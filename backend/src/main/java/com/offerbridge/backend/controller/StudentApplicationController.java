package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.UniversityDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.UniversityService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student/application-list")
@Validated
public class StudentApplicationController {
  private final UniversityService universityService;

  public StudentApplicationController(UniversityService universityService) {
    this.universityService = universityService;
  }

  @GetMapping
  public ApiResponse<UniversityDtos.ApplicationListView> getApplicationList() {
    return ApiResponse.ok(universityService.getApplicationList(AuthContext.getUserId()));
  }

  @PostMapping
  public ApiResponse<UniversityDtos.ApplicationListView> addApplication(
    @Valid @RequestBody UniversityDtos.AddApplicationRequest request
  ) {
    return ApiResponse.ok(universityService.addApplication(AuthContext.getUserId(), request));
  }

  @PutMapping("/{applicationId}/status")
  public ApiResponse<UniversityDtos.ApplicationListView> updateStatus(
    @PathVariable Long applicationId,
    @Valid @RequestBody UniversityDtos.UpdateApplicationStatusRequest request
  ) {
    return ApiResponse.ok(
      universityService.updateApplicationStatus(AuthContext.getUserId(), applicationId, request.getStatusCode())
    );
  }

  @DeleteMapping("/{applicationId}")
  public ApiResponse<UniversityDtos.ApplicationListView> deleteApplication(@PathVariable Long applicationId) {
    return ApiResponse.ok(universityService.removeApplication(AuthContext.getUserId(), applicationId));
  }
}
