package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.StudentDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/student")
@Validated
public class StudentController {
  private final StudentService studentService;
  private final JdbcTemplate jdbcTemplate;
  private final Environment environment;

  public StudentController(StudentService studentService, JdbcTemplate jdbcTemplate, Environment environment) {
    this.studentService = studentService;
    this.jdbcTemplate = jdbcTemplate;
    this.environment = environment;
  }

  @GetMapping("/profile")
  public ApiResponse<StudentDtos.ProfileView> getProfile() {
    return ApiResponse.ok(studentService.getProfile(AuthContext.getUserId()));
  }

  @PutMapping("/profile/basic")
  public ApiResponse<StudentDtos.ProfileView> updateBasicProfile(@Valid @RequestBody StudentDtos.ProfileBasicUpdateRequest request) {
    return ApiResponse.ok(studentService.updateBasicProfile(AuthContext.getUserId(), request));
  }

  @PutMapping("/profile/academic")
  public ApiResponse<StudentDtos.ProfileView> updateAcademicProfile(@Valid @RequestBody StudentDtos.ProfileAcademicUpdateRequest request) {
    return ApiResponse.ok(studentService.updateAcademicProfile(AuthContext.getUserId(), request));
  }

  @GetMapping("/research")
  public ApiResponse<StudentDtos.ResearchView> getResearch() {
    return ApiResponse.ok(studentService.getResearch(AuthContext.getUserId()));
  }

  @PutMapping("/research")
  public ApiResponse<StudentDtos.ResearchView> saveResearch(@Valid @RequestBody StudentDtos.ResearchSaveRequest request) {
    return ApiResponse.ok(studentService.saveResearch(AuthContext.getUserId(), request));
  }

  @GetMapping("/competition")
  public ApiResponse<StudentDtos.CompetitionView> getCompetition() {
    return ApiResponse.ok(studentService.getCompetition(AuthContext.getUserId()));
  }

  @PutMapping("/competition")
  public ApiResponse<StudentDtos.CompetitionView> saveCompetition(@Valid @RequestBody StudentDtos.CompetitionSaveRequest request) {
    return ApiResponse.ok(studentService.saveCompetition(AuthContext.getUserId(), request));
  }

  @GetMapping("/work")
  public ApiResponse<StudentDtos.WorkView> getWork() {
    return ApiResponse.ok(studentService.getWork(AuthContext.getUserId()));
  }

  @PutMapping("/work")
  public ApiResponse<StudentDtos.WorkView> saveWork(@Valid @RequestBody StudentDtos.WorkSaveRequest request) {
    return ApiResponse.ok(studentService.saveWork(AuthContext.getUserId(), request));
  }

  @GetMapping("/exchange")
  public ApiResponse<StudentDtos.ExchangeExperienceItem> getExchangeExperience() {
    return ApiResponse.ok(studentService.getExchangeExperience(AuthContext.getUserId()));
  }

  @PutMapping("/exchange")
  public ApiResponse<StudentDtos.ExchangeExperienceItem> saveExchangeExperience(
    @Valid @RequestBody StudentDtos.ExchangeExperienceSaveRequest request
  ) {
    return ApiResponse.ok(studentService.saveExchangeExperience(AuthContext.getUserId(), request));
  }

  @PostMapping("/verification/submit")
  public ApiResponse<Void> submitVerification(@Valid @RequestBody StudentDtos.VerificationSubmitRequest request) {
    studentService.submitVerification(AuthContext.getUserId(), request);
    return ApiResponse.ok();
  }

  @GetMapping("/verification/status")
  public ApiResponse<StudentDtos.VerificationStatusView> getVerificationStatus() {
    return ApiResponse.ok(studentService.getVerificationStatus(AuthContext.getUserId()));
  }

  @GetMapping("/debug/source-check")
  public ApiResponse<StudentDtos.DebugSourceCheckView> debugSourceCheck() {
    StudentDtos.DebugSourceCheckView view = new StudentDtos.DebugSourceCheckView();
    if (!isDevProfile()) {
      view.setEnabled(false);
      view.setDatabaseName(null);
      view.setResearchTable("ERROR");
      view.setCompetitionTable("ERROR");
      view.setWorkTable("ERROR");
      view.setExchangeTable("ERROR");
      return ApiResponse.ok(view);
    }

    view.setEnabled(true);
    view.setDatabaseName(queryDatabaseName());
    view.setResearchTable(checkTable("student_research_experience"));
    view.setCompetitionTable(checkTable("student_competition_experience"));
    view.setWorkTable(checkTable("student_work_experience"));
    view.setExchangeTable(checkTable("student_exchange_experience"));
    return ApiResponse.ok(view);
  }

  private boolean isDevProfile() {
    return Arrays.stream(environment.getActiveProfiles()).anyMatch("dev"::equalsIgnoreCase);
  }

  private String queryDatabaseName() {
    try {
      return jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
    } catch (Exception ex) {
      return null;
    }
  }

  private String checkTable(String tableName) {
    try {
      Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
        Integer.class,
        tableName
      );
      return count != null && count > 0 ? "OK" : "MISSING";
    } catch (Exception ex) {
      return "ERROR";
    }
  }
}
