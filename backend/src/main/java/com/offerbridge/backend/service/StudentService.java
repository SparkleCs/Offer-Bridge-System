package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.StudentDtos;

public interface StudentService {
  StudentDtos.ProfileView getProfile(Long userId);
  StudentDtos.ProfileView updateBasicProfile(Long userId, StudentDtos.ProfileBasicUpdateRequest request);
  StudentDtos.ProfileView updateAcademicProfile(Long userId, StudentDtos.ProfileAcademicUpdateRequest request);
  StudentDtos.ResearchView getResearch(Long userId);
  StudentDtos.ResearchView saveResearch(Long userId, StudentDtos.ResearchSaveRequest request);
  StudentDtos.CompetitionView getCompetition(Long userId);
  StudentDtos.CompetitionView saveCompetition(Long userId, StudentDtos.CompetitionSaveRequest request);
  StudentDtos.WorkView getWork(Long userId);
  StudentDtos.WorkView saveWork(Long userId, StudentDtos.WorkSaveRequest request);
  StudentDtos.ExchangeExperienceItem getExchangeExperience(Long userId);
  StudentDtos.ExchangeExperienceItem saveExchangeExperience(Long userId, StudentDtos.ExchangeExperienceSaveRequest request);
  void submitVerification(Long userId, StudentDtos.VerificationSubmitRequest request);
  StudentDtos.VerificationStatusView getVerificationStatus(Long userId);
}
