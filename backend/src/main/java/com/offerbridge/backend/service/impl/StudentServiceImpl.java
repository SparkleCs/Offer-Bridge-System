package com.offerbridge.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offerbridge.backend.dto.StudentDtos;
import com.offerbridge.backend.entity.StudentBackgroundScore;
import com.offerbridge.backend.entity.StudentCompetitionExperience;
import com.offerbridge.backend.entity.StudentExchangeExperience;
import com.offerbridge.backend.entity.StudentLanguageScore;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.StudentPublication;
import com.offerbridge.backend.entity.StudentResearchExperience;
import com.offerbridge.backend.entity.StudentTargetCountry;
import com.offerbridge.backend.entity.StudentVerificationMaterial;
import com.offerbridge.backend.entity.StudentWorkExperience;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.entity.VerificationRecord;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.StudentCompetitionExperienceMapper;
import com.offerbridge.backend.mapper.StudentExchangeExperienceMapper;
import com.offerbridge.backend.mapper.StudentLanguageScoreMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.StudentPublicationMapper;
import com.offerbridge.backend.mapper.StudentResearchExperienceMapper;
import com.offerbridge.backend.mapper.StudentTargetCountryMapper;
import com.offerbridge.backend.mapper.StudentVerificationMaterialMapper;
import com.offerbridge.backend.mapper.StudentWorkExperienceMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.mapper.VerificationRecordMapper;
import com.offerbridge.backend.service.StudentBackgroundScoreService;
import com.offerbridge.backend.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
  private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
  private static final Set<String> ALLOWED_LANGUAGE_TYPES = Set.of("CET4", "CET6", "TOEFL", "IELTS", "PTE");
  private static final Set<String> ALLOWED_GPA_SCALES = Set.of("FOUR_POINT", "PERCENTAGE");
  private static final Set<String> ALLOWED_EDUCATION_LEVELS = Set.of("HIGH_SCHOOL", "COLLEGE", "UNDERGRAD", "MASTER", "PHD", "OTHER");

  private final UserAccountMapper userAccountMapper;
  private final StudentProfileMapper studentProfileMapper;
  private final StudentLanguageScoreMapper studentLanguageScoreMapper;
  private final StudentTargetCountryMapper studentTargetCountryMapper;
  private final StudentResearchExperienceMapper studentResearchExperienceMapper;
  private final StudentPublicationMapper studentPublicationMapper;
  private final StudentCompetitionExperienceMapper studentCompetitionExperienceMapper;
  private final StudentWorkExperienceMapper studentWorkExperienceMapper;
  private final StudentExchangeExperienceMapper studentExchangeExperienceMapper;
  private final StudentVerificationMaterialMapper studentVerificationMaterialMapper;
  private final VerificationRecordMapper verificationRecordMapper;
  private final StudentBackgroundScoreService studentBackgroundScoreService;
  private final ObjectMapper objectMapper;

  public StudentServiceImpl(UserAccountMapper userAccountMapper,
                            StudentProfileMapper studentProfileMapper,
                            StudentLanguageScoreMapper studentLanguageScoreMapper,
                            StudentTargetCountryMapper studentTargetCountryMapper,
                            StudentResearchExperienceMapper studentResearchExperienceMapper,
                            StudentPublicationMapper studentPublicationMapper,
                            StudentCompetitionExperienceMapper studentCompetitionExperienceMapper,
                            StudentWorkExperienceMapper studentWorkExperienceMapper,
                            StudentExchangeExperienceMapper studentExchangeExperienceMapper,
                            StudentVerificationMaterialMapper studentVerificationMaterialMapper,
                            VerificationRecordMapper verificationRecordMapper,
                            StudentBackgroundScoreService studentBackgroundScoreService,
                            ObjectMapper objectMapper) {
    this.userAccountMapper = userAccountMapper;
    this.studentProfileMapper = studentProfileMapper;
    this.studentLanguageScoreMapper = studentLanguageScoreMapper;
    this.studentTargetCountryMapper = studentTargetCountryMapper;
    this.studentResearchExperienceMapper = studentResearchExperienceMapper;
    this.studentPublicationMapper = studentPublicationMapper;
    this.studentCompetitionExperienceMapper = studentCompetitionExperienceMapper;
    this.studentWorkExperienceMapper = studentWorkExperienceMapper;
    this.studentExchangeExperienceMapper = studentExchangeExperienceMapper;
    this.studentVerificationMaterialMapper = studentVerificationMaterialMapper;
    this.verificationRecordMapper = verificationRecordMapper;
    this.studentBackgroundScoreService = studentBackgroundScoreService;
    this.objectMapper = objectMapper;
  }

  @Override
  public StudentDtos.ProfileView getProfile(Long userId) {
    UserAccount user = requireUser(userId);
    StudentProfile profile = ensureProfile(userId);
    List<StudentLanguageScore> scores = studentLanguageScoreMapper.listByUserId(userId);
    List<StudentTargetCountry> countries = studentTargetCountryMapper.listByUserId(userId);
    return toView(user, profile, scores, countries);
  }

  @Override
  @Transactional
  public StudentDtos.ProfileView updateBasicProfile(Long userId, StudentDtos.ProfileBasicUpdateRequest request) {
    UserAccount user = requireUser(userId);
    StudentProfile profile = ensureProfile(userId);

    validateBudget(request.getBudgetMin(), request.getBudgetMax());
    if (notBlank(request.getEducationLevel()) && !ALLOWED_EDUCATION_LEVELS.contains(request.getEducationLevel())) {
      throw new BizException("BIZ_BAD_REQUEST", "学历层次不支持");
    }

    profile.setName(request.getName());
    profile.setEmail(request.getEmail());
    profile.setWechatId(trimToNull(request.getWechatId()));
    profile.setEducationLevel(request.getEducationLevel());
    profile.setSchoolName(request.getSchoolName());
    profile.setUndergraduateSchoolTier(trimToNull(request.getUndergraduateSchoolTier()));
    profile.setMajor(request.getMajor());
    profile.setTargetMajorText(request.getTargetMajorText());
    profile.setIntakeTerm(request.getIntakeTerm());
    profile.setBudgetCurrency(request.getBudgetCurrency());
    profile.setBudgetMin(request.getBudgetMin());
    profile.setBudgetMax(request.getBudgetMax());
    profile.setBudgetNote(request.getBudgetNote());
    studentProfileMapper.updateProfile(profile);

    studentTargetCountryMapper.deleteByUserId(userId);
    if (request.getTargetCountries() != null) {
      for (StudentDtos.TargetCountryItem item : request.getTargetCountries()) {
        if (notBlank(item.getCountryName())) {
          studentTargetCountryMapper.insertOne(userId, item.getCountryName().trim());
        }
      }
    }

    List<StudentLanguageScore> scores = studentLanguageScoreMapper.listByUserId(userId);
    List<StudentTargetCountry> countries = studentTargetCountryMapper.listByUserId(userId);
    StudentProfile latest = studentProfileMapper.findByUserId(userId);
    refreshBackgroundScoreQuietly(userId, "basic-profile");

    return toView(user, latest, scores, countries);
  }

  @Override
  @Transactional
  public StudentDtos.ProfileView updateWechat(Long userId, StudentDtos.WechatUpdateRequest request) {
    UserAccount user = requireUser(userId);
    ensureProfile(userId);
    studentProfileMapper.updateWechatId(userId, request.getWechatId().trim());
    StudentProfile latest = studentProfileMapper.findByUserId(userId);
    List<StudentLanguageScore> scores = studentLanguageScoreMapper.listByUserId(userId);
    List<StudentTargetCountry> countries = studentTargetCountryMapper.listByUserId(userId);
    return toView(user, latest, scores, countries);
  }

  @Override
  @Transactional
  public StudentDtos.ProfileView updateAcademicProfile(Long userId, StudentDtos.ProfileAcademicUpdateRequest request) {
    UserAccount user = requireUser(userId);
    StudentProfile profile = ensureProfile(userId);

    validateGpa(request.getGpaScale(), request.getGpaValue());
    validateLanguageScores(request.getLanguageScores());
    validateRank(request.getRankValue());

    profile.setGpaValue(request.getGpaValue());
    profile.setGpaScale(request.getGpaScale());
    profile.setRankValue(request.getRankValue());
    studentProfileMapper.updateProfile(profile);

    studentLanguageScoreMapper.deleteByUserId(userId);
    for (StudentDtos.LanguageScoreItem item : request.getLanguageScores()) {
      studentLanguageScoreMapper.insertOne(userId, item.getLanguageType(), item.getScore());
    }

    List<StudentLanguageScore> scores = studentLanguageScoreMapper.listByUserId(userId);
    List<StudentTargetCountry> countries = studentTargetCountryMapper.listByUserId(userId);
    StudentProfile latest = studentProfileMapper.findByUserId(userId);
    refreshBackgroundScoreQuietly(userId, "academic-profile");
    return toView(user, latest, scores, countries);
  }

  @Override
  public StudentDtos.ResearchView getResearch(Long userId) {
    requireUser(userId);
    return buildResearchView(userId);
  }

  @Override
  @Transactional
  public StudentDtos.ResearchView saveResearch(Long userId, StudentDtos.ResearchSaveRequest request) {
    requireUser(userId);

    studentPublicationMapper.deleteByUserId(userId);
    studentResearchExperienceMapper.deleteByUserId(userId);

    List<StudentDtos.ResearchItem> items = request.getItems() == null ? List.of() : request.getItems();
    for (StudentDtos.ResearchItem item : items) {
      StudentResearchExperience experience = new StudentResearchExperience();
      experience.setUserId(userId);
      experience.setProjectName(item.getProjectName());
      experience.setRoleName(item.getRoleName());
      experience.setRoleLevel(item.getRoleLevel());
      experience.setRelevanceLevel(item.getRelevanceLevel());
      experience.setStartDate(item.getStartDate());
      experience.setEndDate(item.getEndDate());
      experience.setDurationMonths(item.getDurationMonths());
      experience.setContentSummary(item.getContentSummary());
      experience.setHasPublication(Boolean.TRUE.equals(item.getHasPublication()));
      studentResearchExperienceMapper.insertOne(experience);

      if (Boolean.TRUE.equals(item.getHasPublication()) && item.getPublications() != null) {
        for (StudentDtos.PublicationItem pub : item.getPublications()) {
          if (!notBlank(pub.getTitle())) continue;
          StudentPublication entity = new StudentPublication();
          entity.setResearchId(experience.getId());
          entity.setTitle(pub.getTitle());
          entity.setAuthorRole(pub.getAuthorRole());
          entity.setAuthorOrder(pub.getAuthorOrder());
          entity.setJournalName(pub.getJournalName());
          entity.setPublicationLevel(pub.getPublicationLevel());
          entity.setJournalPartition(pub.getJournalPartition());
          entity.setPublishedYear(pub.getPublishedYear());
          entity.setIndexedInfo(pub.getIndexedInfo());
          studentPublicationMapper.insertOne(entity);
        }
      }
    }

    refreshBackgroundScoreQuietly(userId, "research");
    return buildResearchView(userId);
  }

  @Override
  public StudentDtos.CompetitionView getCompetition(Long userId) {
    requireUser(userId);
    List<StudentCompetitionExperience> list = studentCompetitionExperienceMapper.listByUserId(userId);
    StudentDtos.CompetitionView view = new StudentDtos.CompetitionView();
    view.setItems(list.stream().map(this::toCompetitionItem).toList());
    return view;
  }

  @Override
  @Transactional
  public StudentDtos.CompetitionView saveCompetition(Long userId, StudentDtos.CompetitionSaveRequest request) {
    requireUser(userId);
    studentCompetitionExperienceMapper.deleteByUserId(userId);

    List<StudentDtos.CompetitionItem> items = request.getItems() == null ? List.of() : request.getItems();
    for (StudentDtos.CompetitionItem item : items) {
      StudentCompetitionExperience entity = new StudentCompetitionExperience();
      entity.setUserId(userId);
      entity.setCompetitionName(item.getCompetitionName());
      entity.setCompetitionLevel(item.getCompetitionLevel());
      entity.setAward(item.getAward());
      entity.setAwardLevel(item.getAwardLevel());
      entity.setRelevanceLevel(item.getRelevanceLevel());
      entity.setRoleDesc(item.getRoleDesc());
      entity.setEventDate(item.getEventDate());
      studentCompetitionExperienceMapper.insertOne(entity);
    }
    refreshBackgroundScoreQuietly(userId, "competition");
    return getCompetition(userId);
  }

  @Override
  public StudentDtos.WorkView getWork(Long userId) {
    requireUser(userId);
    return buildWorkView(userId);
  }

  @Override
  @Transactional
  public StudentDtos.WorkView saveWork(Long userId, StudentDtos.WorkSaveRequest request) {
    requireUser(userId);

    studentWorkExperienceMapper.deleteByUserId(userId);

    List<StudentDtos.WorkItem> items = request.getItems() == null ? List.of() : request.getItems();
    for (StudentDtos.WorkItem item : items) {
      StudentWorkExperience entity = new StudentWorkExperience();
      entity.setUserId(userId);
      entity.setCompanyName(item.getCompanyName());
      entity.setCompanyTier(item.getCompanyTier());
      entity.setPositionName(item.getPositionName());
      entity.setRelevanceLevel(item.getRelevanceLevel());
      entity.setTitleLevel(item.getTitleLevel());
      entity.setStartDate(item.getStartDate());
      entity.setEndDate(item.getEndDate());
      entity.setDurationMonths(item.getDurationMonths());
      entity.setKeywords(item.getKeywords());
      entity.setContentSummary(item.getContentSummary());
      studentWorkExperienceMapper.insertOne(entity);
    }

    refreshBackgroundScoreQuietly(userId, "work");
    return buildWorkView(userId);
  }

  @Override
  public StudentDtos.ExchangeExperienceItem getExchangeExperience(Long userId) {
    requireUser(userId);
    StudentExchangeExperience entity = studentExchangeExperienceMapper.findByUserId(userId);
    return toExchangeItem(entity);
  }

  @Override
  @Transactional
  public StudentDtos.ExchangeExperienceItem saveExchangeExperience(Long userId, StudentDtos.ExchangeExperienceSaveRequest request) {
    requireUser(userId);
    validateExchangeGpa(request.getGpaValue());
    StudentExchangeExperience entity = new StudentExchangeExperience();
    entity.setUserId(userId);
    entity.setCountryName(request.getCountryName().trim());
    entity.setUniversityName(request.getUniversityName().trim());
    entity.setSchoolTier(trimToNull(request.getSchoolTier()));
    entity.setGpaValue(request.getGpaValue());
    entity.setMajorCourses(request.getMajorCourses().trim());
    entity.setRelevanceLevel(trimToNull(request.getRelevanceLevel()));
    entity.setStartDate(request.getStartDate().trim());
    entity.setEndDate(request.getEndDate().trim());
    entity.setDurationMonths(request.getDurationMonths());
    studentExchangeExperienceMapper.upsert(entity);
    refreshBackgroundScoreQuietly(userId, "exchange");
    return toExchangeItem(studentExchangeExperienceMapper.findByUserId(userId));
  }

  @Override
  public StudentDtos.BackgroundScoreView getBackgroundScore(Long userId) {
    requireUser(userId);
    return toBackgroundScoreView(studentBackgroundScoreService.getOrRefreshScore(userId));
  }

  @Override
  @Transactional
  public StudentDtos.BackgroundScoreView refreshBackgroundScore(Long userId) {
    requireUser(userId);
    return toBackgroundScoreView(studentBackgroundScoreService.refreshScore(userId));
  }

  @Override
  @Transactional
  public void submitVerification(Long userId, StudentDtos.VerificationSubmitRequest request) {
    requireUser(userId);

    StudentVerificationMaterial material = new StudentVerificationMaterial();
    material.setUserId(userId);
    material.setRealName(request.getRealName());
    material.setIdNoMasked(mask(request.getIdNo()));
    material.setIdCardImageUrl(request.getIdCardImageUrl());
    material.setStudentCardImageUrl(request.getStudentCardImageUrl());
    studentVerificationMaterialMapper.upsert(material);

    Map<String, String> payload = new LinkedHashMap<>();
    payload.put("realName", request.getRealName());
    payload.put("idNo", mask(request.getIdNo()));
    payload.put("idCardImageUrl", request.getIdCardImageUrl());
    payload.put("studentCardImageUrl", request.getStudentCardImageUrl());

    String payloadJson;
    try {
      payloadJson = objectMapper.writeValueAsString(payload);
    } catch (Exception ex) {
      throw new BizException("BIZ_INTERNAL_ERROR", "系统异常");
    }

    LocalDateTime now = LocalDateTime.now();

    VerificationRecord realName = new VerificationRecord();
    realName.setUserId(userId);
    realName.setVerifyType("REAL_NAME");
    realName.setStatus("PENDING");
    realName.setPayloadJson(payloadJson);
    realName.setSubmittedAt(now);
    verificationRecordMapper.upsert(realName);

    VerificationRecord education = new VerificationRecord();
    education.setUserId(userId);
    education.setVerifyType("EDUCATION");
    education.setStatus("PENDING");
    education.setPayloadJson(payloadJson);
    education.setSubmittedAt(now);
    verificationRecordMapper.upsert(education);
  }

  @Override
  public StudentDtos.VerificationStatusView getVerificationStatus(Long userId) {
    requireUser(userId);
    VerificationRecord realName = verificationRecordMapper.findOne(userId, "REAL_NAME");
    VerificationRecord education = verificationRecordMapper.findOne(userId, "EDUCATION");

    String realNameStatus = realName == null ? "UNVERIFIED" : realName.getStatus();
    String educationStatus = education == null ? "UNVERIFIED" : education.getStatus();

    StudentDtos.VerificationStatusView view = new StudentDtos.VerificationStatusView();
    view.setRealNameStatus(realNameStatus);
    view.setEducationStatus(educationStatus);
    view.setVerificationCompleted("APPROVED".equals(realNameStatus) && "APPROVED".equals(educationStatus));
    return view;
  }

  private StudentDtos.ResearchView buildResearchView(Long userId) {
    List<StudentResearchExperience> experiences = studentResearchExperienceMapper.listByUserId(userId);
    List<StudentPublication> publications = studentPublicationMapper.listByUserId(userId);

    Map<Long, List<StudentDtos.PublicationItem>> publicationMap = new LinkedHashMap<>();
    for (StudentPublication publication : publications) {
      publicationMap.computeIfAbsent(publication.getResearchId(), k -> new ArrayList<>()).add(toPublicationItem(publication));
    }

    List<StudentDtos.ResearchItem> items = experiences.stream().map(exp -> {
      StudentDtos.ResearchItem item = new StudentDtos.ResearchItem();
      item.setId(exp.getId());
      item.setProjectName(exp.getProjectName());
      item.setRoleName(exp.getRoleName());
      item.setRoleLevel(exp.getRoleLevel());
      item.setRelevanceLevel(exp.getRelevanceLevel());
      item.setStartDate(exp.getStartDate());
      item.setEndDate(exp.getEndDate());
      item.setDurationMonths(exp.getDurationMonths());
      item.setContentSummary(exp.getContentSummary());
      item.setHasPublication(Boolean.TRUE.equals(exp.getHasPublication()));
      item.setPublications(publicationMap.getOrDefault(exp.getId(), List.of()));
      return item;
    }).toList();

    StudentDtos.ResearchView view = new StudentDtos.ResearchView();
    view.setItems(items);
    return view;
  }

  private StudentDtos.WorkView buildWorkView(Long userId) {
    List<StudentWorkExperience> works = studentWorkExperienceMapper.listByUserId(userId);

    List<StudentDtos.WorkItem> items = works.stream().map(work -> {
      StudentDtos.WorkItem item = new StudentDtos.WorkItem();
      item.setId(work.getId());
      item.setCompanyName(work.getCompanyName());
      item.setCompanyTier(work.getCompanyTier());
      item.setPositionName(work.getPositionName());
      item.setRelevanceLevel(work.getRelevanceLevel());
      item.setTitleLevel(work.getTitleLevel());
      item.setStartDate(work.getStartDate());
      item.setEndDate(work.getEndDate());
      item.setDurationMonths(work.getDurationMonths());
      item.setKeywords(work.getKeywords());
      item.setContentSummary(work.getContentSummary());
      return item;
    }).toList();

    StudentDtos.WorkView view = new StudentDtos.WorkView();
    view.setItems(items);
    return view;
  }

  private StudentProfile ensureProfile(Long userId) {
    StudentProfile profile = studentProfileMapper.findByUserId(userId);
    if (profile == null) {
      studentProfileMapper.insertEmpty(userId);
      profile = studentProfileMapper.findByUserId(userId);
    }
    return profile;
  }

  private StudentDtos.ProfileView toView(UserAccount user,
                                         StudentProfile profile,
                                         List<StudentLanguageScore> scores,
                                         List<StudentTargetCountry> countries) {
    StudentDtos.ProfileView view = new StudentDtos.ProfileView();
    view.setUserId(user.getId());
    view.setPhone(user.getPhone());
    view.setName(profile.getName());
    view.setEmail(profile.getEmail());
    view.setWechatId(profile.getWechatId());
    view.setEducationLevel(profile.getEducationLevel());
    view.setSchoolName(profile.getSchoolName());
    view.setUndergraduateSchoolTier(profile.getUndergraduateSchoolTier());
    view.setMajor(profile.getMajor());
    view.setGpaValue(profile.getGpaValue());
    view.setGpaScale(profile.getGpaScale());
    view.setLanguageScores(scores.stream().map(this::toLanguageItem).toList());
    view.setRankValue(profile.getRankValue());
    view.setTargetMajorText(profile.getTargetMajorText());
    view.setIntakeTerm(profile.getIntakeTerm());
    view.setBudgetCurrency(profile.getBudgetCurrency());
    view.setBudgetMin(profile.getBudgetMin());
    view.setBudgetMax(profile.getBudgetMax());
    view.setBudgetNote(profile.getBudgetNote());
    view.setTargetCountries(countries.stream().map(this::toTargetCountryItem).toList());
    view.setProfileCompleted(
      notBlank(profile.getName())
        && notBlank(profile.getEducationLevel())
        && notBlank(profile.getSchoolName())
        && notBlank(profile.getMajor())
        && profile.getGpaValue() != null
        && notBlank(profile.getGpaScale())
        && !scores.isEmpty()
    );
    return view;
  }

  private StudentDtos.TargetCountryItem toTargetCountryItem(StudentTargetCountry country) {
    StudentDtos.TargetCountryItem item = new StudentDtos.TargetCountryItem();
    item.setCountryName(country.getCountryName());
    return item;
  }

  private StudentDtos.LanguageScoreItem toLanguageItem(StudentLanguageScore score) {
    StudentDtos.LanguageScoreItem item = new StudentDtos.LanguageScoreItem();
    item.setLanguageType(score.getLanguageType());
    item.setScore(score.getScore());
    return item;
  }

  private StudentDtos.PublicationItem toPublicationItem(StudentPublication publication) {
    StudentDtos.PublicationItem item = new StudentDtos.PublicationItem();
    item.setTitle(publication.getTitle());
    item.setAuthorRole(publication.getAuthorRole());
    item.setAuthorOrder(publication.getAuthorOrder());
    item.setJournalName(publication.getJournalName());
    item.setPublicationLevel(publication.getPublicationLevel());
    item.setJournalPartition(publication.getJournalPartition());
    item.setPublishedYear(publication.getPublishedYear());
    item.setIndexedInfo(publication.getIndexedInfo());
    return item;
  }

  private StudentDtos.CompetitionItem toCompetitionItem(StudentCompetitionExperience entity) {
    StudentDtos.CompetitionItem item = new StudentDtos.CompetitionItem();
    item.setId(entity.getId());
    item.setCompetitionName(entity.getCompetitionName());
    item.setCompetitionLevel(entity.getCompetitionLevel());
    item.setAward(entity.getAward());
    item.setAwardLevel(entity.getAwardLevel());
    item.setRelevanceLevel(entity.getRelevanceLevel());
    item.setRoleDesc(entity.getRoleDesc());
    item.setEventDate(entity.getEventDate());
    return item;
  }

  private StudentDtos.ExchangeExperienceItem toExchangeItem(StudentExchangeExperience entity) {
    StudentDtos.ExchangeExperienceItem item = new StudentDtos.ExchangeExperienceItem();
    if (entity == null) {
      return item;
    }
    item.setCountryName(entity.getCountryName());
    item.setUniversityName(entity.getUniversityName());
    item.setSchoolTier(entity.getSchoolTier());
    item.setGpaValue(entity.getGpaValue());
    item.setMajorCourses(entity.getMajorCourses());
    item.setRelevanceLevel(entity.getRelevanceLevel());
    item.setStartDate(entity.getStartDate());
    item.setEndDate(entity.getEndDate());
    item.setDurationMonths(entity.getDurationMonths());
    return item;
  }

  private StudentDtos.BackgroundScoreView toBackgroundScoreView(StudentBackgroundScore score) {
    StudentDtos.BackgroundScoreView view = new StudentDtos.BackgroundScoreView();
    view.setGpaScore(score.getGpaScore());
    view.setLanguageScore(score.getLanguageScore());
    view.setPublicationScore(score.getPublicationScore());
    view.setResearchScore(score.getResearchScore());
    view.setInternshipScore(score.getInternshipScore());
    view.setExchangeScore(score.getExchangeScore());
    view.setCompetitionScore(score.getCompetitionScore());
    view.setUndergraduateSchoolScore(score.getUndergraduateSchoolScore());
    view.setOverallAcademicScore(score.getOverallAcademicScore());
    view.setScoreVersion(score.getScoreVersion());
    view.setScoreDetailJson(score.getScoreDetailJson());
    return view;
  }

  private void refreshBackgroundScoreQuietly(Long userId, String source) {
    try {
      studentBackgroundScoreService.refreshScore(userId);
    } catch (Exception ex) {
      log.warn("Student background score refresh failed after {} save, userId={}", source, userId, ex);
    }
  }

  private void validateLanguageScores(List<StudentDtos.LanguageScoreItem> items) {
    if (items == null || items.isEmpty()) {
      throw new BizException("BIZ_BAD_REQUEST", "请至少填写一项语言成绩");
    }
    Set<String> types = items.stream().map(StudentDtos.LanguageScoreItem::getLanguageType).collect(Collectors.toSet());
    if (types.size() != items.size()) {
      throw new BizException("BIZ_BAD_REQUEST", "语言类型不能重复");
    }

    for (StudentDtos.LanguageScoreItem item : items) {
      String type = item.getLanguageType();
      if (!ALLOWED_LANGUAGE_TYPES.contains(type)) {
        throw new BizException("BIZ_BAD_REQUEST", "语言类型不支持");
      }
      BigDecimal score = item.getScore();
      if (score == null) {
        throw new BizException("BIZ_BAD_REQUEST", "语言分数不能为空");
      }
      if (!isLanguageScoreInRange(type, score)) {
        throw new BizException("BIZ_BAD_REQUEST", "语言分数超出范围");
      }
    }
  }

  private void validateGpa(String gpaScale, BigDecimal gpaValue) {
    if (!ALLOWED_GPA_SCALES.contains(gpaScale) || gpaValue == null) {
      throw new BizException("BIZ_BAD_REQUEST", "GPA 参数不合法");
    }

    if ("FOUR_POINT".equals(gpaScale) && (gpaValue.compareTo(BigDecimal.ZERO) < 0 || gpaValue.compareTo(new BigDecimal("4.00")) > 0)) {
      throw new BizException("BIZ_BAD_REQUEST", "4.0 制 GPA 范围应为 0-4.00");
    }
    if ("PERCENTAGE".equals(gpaScale) && (gpaValue.compareTo(BigDecimal.ZERO) < 0 || gpaValue.compareTo(new BigDecimal("100.00")) > 0)) {
      throw new BizException("BIZ_BAD_REQUEST", "百分制 GPA 范围应为 0-100");
    }
  }

  private void validateBudget(BigDecimal budgetMin, BigDecimal budgetMax) {
    if (budgetMin != null && budgetMax != null && budgetMin.compareTo(budgetMax) > 0) {
      throw new BizException("BIZ_BAD_REQUEST", "预算最小值不能大于最大值");
    }
  }

  private void validateRank(Integer rankValue) {
    if (rankValue == null) return;
    if (rankValue <= 0 || rankValue > 100) {
      throw new BizException("BIZ_BAD_REQUEST", "排名百分位应在 1-100 之间");
    }
  }

  private void validateExchangeGpa(BigDecimal gpaValue) {
    if (gpaValue == null || gpaValue.compareTo(BigDecimal.ZERO) < 0 || gpaValue.compareTo(new BigDecimal("4.00")) > 0) {
      throw new BizException("BIZ_BAD_REQUEST", "交换经历 GPA 范围应为 0-4.00");
    }
  }

  private boolean isLanguageScoreInRange(String type, BigDecimal score) {
    return switch (type) {
      case "IELTS" -> score.compareTo(BigDecimal.ZERO) >= 0 && score.compareTo(new BigDecimal("9")) <= 0;
      case "TOEFL" -> score.compareTo(BigDecimal.ZERO) >= 0 && score.compareTo(new BigDecimal("120")) <= 0;
      case "PTE" -> score.compareTo(new BigDecimal("10")) >= 0 && score.compareTo(new BigDecimal("90")) <= 0;
      case "CET4", "CET6" -> score.compareTo(BigDecimal.ZERO) >= 0 && score.compareTo(new BigDecimal("710")) <= 0;
      default -> false;
    };
  }

  private UserAccount requireUser(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "用户不存在");
    }
    if (!"STUDENT".equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "当前账号非学生角色");
    }
    return user;
  }

  private String mask(String value) {
    if (value == null || value.length() < 7) return "***";
    return value.substring(0, 3) + "****" + value.substring(value.length() - 2);
  }

  private boolean notBlank(String value) {
    return value != null && !value.isBlank();
  }

  private String trimToNull(String value) {
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }
}
