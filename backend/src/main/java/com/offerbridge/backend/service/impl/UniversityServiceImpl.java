package com.offerbridge.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offerbridge.backend.dto.UniversityDtos;
import com.offerbridge.backend.entity.Program;
import com.offerbridge.backend.entity.School;
import com.offerbridge.backend.entity.StudentApplicationProgramRow;
import com.offerbridge.backend.entity.StudentLanguageScore;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.StudentTargetCountry;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.ProgramMapper;
import com.offerbridge.backend.mapper.SchoolMapper;
import com.offerbridge.backend.mapper.StudentApplicationListMapper;
import com.offerbridge.backend.mapper.StudentLanguageScoreMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.StudentProgramMatchResultMapper;
import com.offerbridge.backend.mapper.StudentTargetCountryMapper;
import com.offerbridge.backend.service.UniversityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UniversityServiceImpl implements UniversityService {
  private static final Set<String> ALLOWED_STATUS = Set.of(
    "COLLECTED", "TO_EVALUATE", "PREPARING", "APPLYING", "SUBMITTED", "ADMITTED", "REJECTED"
  );

  private final SchoolMapper schoolMapper;
  private final ProgramMapper programMapper;
  private final StudentProfileMapper studentProfileMapper;
  private final StudentLanguageScoreMapper studentLanguageScoreMapper;
  private final StudentTargetCountryMapper studentTargetCountryMapper;
  private final StudentApplicationListMapper studentApplicationListMapper;
  private final StudentProgramMatchResultMapper studentProgramMatchResultMapper;
  private final ObjectMapper objectMapper;

  public UniversityServiceImpl(SchoolMapper schoolMapper,
                               ProgramMapper programMapper,
                               StudentProfileMapper studentProfileMapper,
                               StudentLanguageScoreMapper studentLanguageScoreMapper,
                               StudentTargetCountryMapper studentTargetCountryMapper,
                               StudentApplicationListMapper studentApplicationListMapper,
                               StudentProgramMatchResultMapper studentProgramMatchResultMapper,
                               ObjectMapper objectMapper) {
    this.schoolMapper = schoolMapper;
    this.programMapper = programMapper;
    this.studentProfileMapper = studentProfileMapper;
    this.studentLanguageScoreMapper = studentLanguageScoreMapper;
    this.studentTargetCountryMapper = studentTargetCountryMapper;
    this.studentApplicationListMapper = studentApplicationListMapper;
    this.studentProgramMatchResultMapper = studentProgramMatchResultMapper;
    this.objectMapper = objectMapper;
  }

  @Override
  public UniversityDtos.MetaView getMeta() {
    UniversityDtos.MetaView view = new UniversityDtos.MetaView();

    view.setCountries(schoolMapper.listCountries().stream().map(item -> {
      UniversityDtos.FilterOption option = new UniversityDtos.FilterOption();
      option.setCode(item.getCountryCode());
      option.setName(item.getCountryName());
      return option;
    }).toList());

    view.setSubjectCategories(schoolMapper.listSubjectCategories().stream().map(item -> {
      UniversityDtos.FilterOption option = new UniversityDtos.FilterOption();
      option.setCode(item.getCategoryCode());
      option.setName(item.getCategoryName());
      return option;
    }).toList());

    view.setDirections(programMapper.listMajorDirections().stream().map(item -> {
      UniversityDtos.FilterOption option = new UniversityDtos.FilterOption();
      option.setCode(item.getDirectionCode());
      option.setName(item.getDirectionName());
      option.setParentCode(item.getSubjectCategoryCode());
      return option;
    }).toList());

    List<UniversityDtos.FilterOption> statusOptions = new ArrayList<>();
    statusOptions.add(buildOption("COLLECTED", "已收藏"));
    statusOptions.add(buildOption("TO_EVALUATE", "待评估"));
    statusOptions.add(buildOption("PREPARING", "准备申请"));
    statusOptions.add(buildOption("APPLYING", "申请中"));
    statusOptions.add(buildOption("SUBMITTED", "已提交"));
    statusOptions.add(buildOption("ADMITTED", "已录取"));
    statusOptions.add(buildOption("REJECTED", "已拒绝"));
    view.setApplicationStatuses(statusOptions);

    return view;
  }

  @Override
  public List<UniversityDtos.SchoolListItem> listSchools(String countryCode,
                                                         String subjectCategoryCode,
                                                         String directionCode,
                                                         Integer rankMin,
                                                         Integer rankMax,
                                                         String keyword) {
    return schoolMapper.listSchools(
        countryCode,
        subjectCategoryCode,
        directionCode,
        rankMin,
        rankMax,
        normalizeKeyword(keyword)
      )
      .stream()
      .map(this::toSchoolListItem)
      .toList();
  }

  @Override
  public UniversityDtos.SchoolDetailView getSchoolDetail(Long schoolId) {
    School school = schoolMapper.findById(schoolId);
    if (school == null) {
      throw new BizException("BIZ_NOT_FOUND", "学校不存在");
    }

    UniversityDtos.SchoolDetailView view = new UniversityDtos.SchoolDetailView();
    view.setId(school.getId());
    view.setSchoolNameCn(school.getSchoolNameCn());
    view.setSchoolNameEn(school.getSchoolNameEn());
    view.setCountryCode(school.getCountryCode());
    view.setCountryName(school.getCountryName());
    view.setCityName(school.getCityName());
    view.setQsRank(school.getQsRank());
    view.setRankingYear(school.getRankingYear());
    view.setSchoolSummary(school.getSchoolSummary());
    view.setTuitionMin(school.getTuitionMin());
    view.setTuitionMax(school.getTuitionMax());
    view.setTuitionCurrency(school.getTuitionCurrency());
    view.setDurationMinMonths(school.getDurationMinMonths());
    view.setDurationMaxMonths(school.getDurationMaxMonths());
    view.setLanguageRequirementRange(school.getLanguageRequirementRange());
    view.setAdvantageSubjects(school.getAdvantageSubjects());
    view.setRepresentativePrograms(
      programMapper.listProgramsBySchoolId(schoolId, 8).stream().map(this::toProgramListItem).toList()
    );
    return view;
  }

  @Override
  public List<UniversityDtos.ProgramListItem> listPrograms(Long schoolId,
                                                           String countryCode,
                                                           String subjectCategoryCode,
                                                           String directionCode,
                                                           String keyword) {
    return programMapper.listPrograms(
        schoolId,
        countryCode,
        subjectCategoryCode,
        directionCode,
        normalizeKeyword(keyword)
      )
      .stream()
      .map(this::toProgramListItem)
      .toList();
  }

  @Override
  public UniversityDtos.ProgramDetailView getProgramDetail(Long userId, Long programId) {
    Program program = programMapper.findById(programId);
    if (program == null) {
      throw new BizException("BIZ_NOT_FOUND", "项目不存在");
    }

    UniversityDtos.ProgramDetailView view = new UniversityDtos.ProgramDetailView();
    view.setBasic(toProgramListItem(program));
    view.setDegreeType(program.getDegreeType());
    view.setRequiresGre(Boolean.TRUE.equals(program.getRequiresGre()));
    view.setRequiresGmat(Boolean.TRUE.equals(program.getRequiresGmat()));
    view.setBackgroundPreference(program.getBackgroundPreference());
    view.setApplicationRoundsOverview(program.getApplicationRoundsOverview());
    view.setSuitableTags(program.getSuitableTags());
    view.setIntakeTerm(program.getIntakeTerm());
    view.setProgramSummary(program.getProgramSummary());
    view.setMatchResult(computeAndPersistMatch(userId, program));

    return view;
  }

  @Override
  public UniversityDtos.ApplicationListView getApplicationList(Long userId) {
    List<StudentApplicationProgramRow> rows = studentApplicationListMapper.listByUserId(userId);
    UniversityDtos.ApplicationListView view = new UniversityDtos.ApplicationListView();
    view.setItems(rows.stream().map(row -> toApplicationItem(userId, row)).toList());
    return view;
  }

  @Override
  @Transactional
  public UniversityDtos.ApplicationListView addApplication(Long userId, UniversityDtos.AddApplicationRequest request) {
    Program program = programMapper.findById(request.getProgramId());
    if (program == null) {
      throw new BizException("BIZ_NOT_FOUND", "项目不存在");
    }
    studentApplicationListMapper.upsert(userId, request.getProgramId(), "COLLECTED", request.getNoteText());
    computeAndPersistMatch(userId, program);
    return getApplicationList(userId);
  }

  @Override
  @Transactional
  public UniversityDtos.ApplicationListView updateApplicationStatus(Long userId, Long applicationId, String statusCode) {
    if (!ALLOWED_STATUS.contains(statusCode)) {
      throw new BizException("BIZ_BAD_REQUEST", "申请状态不支持");
    }
    int updated = studentApplicationListMapper.updateStatus(userId, applicationId, statusCode);
    if (updated == 0) {
      throw new BizException("BIZ_NOT_FOUND", "申请清单记录不存在");
    }
    return getApplicationList(userId);
  }

  @Override
  @Transactional
  public UniversityDtos.ApplicationListView removeApplication(Long userId, Long applicationId) {
    studentApplicationListMapper.deleteById(userId, applicationId);
    return getApplicationList(userId);
  }

  private UniversityDtos.SchoolListItem toSchoolListItem(School school) {
    UniversityDtos.SchoolListItem item = new UniversityDtos.SchoolListItem();
    item.setId(school.getId());
    item.setSchoolNameCn(school.getSchoolNameCn());
    item.setSchoolNameEn(school.getSchoolNameEn());
    item.setCountryCode(school.getCountryCode());
    item.setCountryName(school.getCountryName());
    item.setCityName(school.getCityName());
    item.setQsRank(school.getQsRank());
    item.setAdvantageSubjects(school.getAdvantageSubjects());
    item.setTuitionMin(school.getTuitionMin());
    item.setTuitionMax(school.getTuitionMax());
    item.setTuitionCurrency(school.getTuitionCurrency());
    return item;
  }

  private UniversityDtos.ProgramListItem toProgramListItem(Program program) {
    UniversityDtos.ProgramListItem item = new UniversityDtos.ProgramListItem();
    item.setId(program.getId());
    item.setSchoolId(program.getSchoolId());
    item.setSchoolNameCn(program.getSchoolNameCn());
    item.setSchoolNameEn(program.getSchoolNameEn());
    item.setQsRank(program.getQsRank());
    item.setCountryCode(program.getCountryCode());
    item.setCountryName(program.getCountryName());
    item.setProgramName(program.getProgramName());
    item.setCollegeName(program.getCollegeName());
    item.setSubjectCategoryCode(program.getSubjectCategoryCode());
    item.setSubjectCategoryName(program.getSubjectCategoryName());
    item.setDirectionCode(program.getDirectionCode());
    item.setDirectionName(program.getDirectionName());
    item.setStudyMode(program.getStudyMode());
    item.setDurationMonths(program.getDurationMonths());
    item.setTuitionAmount(program.getTuitionAmount());
    item.setTuitionCurrency(program.getTuitionCurrency());
    item.setLanguageType(program.getLanguageType());
    item.setLanguageMinScore(program.getLanguageMinScore());
    item.setGpaMinRecommend(program.getGpaMinRecommend());
    return item;
  }

  private UniversityDtos.ApplicationListItem toApplicationItem(Long userId, StudentApplicationProgramRow row) {
    UniversityDtos.ApplicationListItem item = new UniversityDtos.ApplicationListItem();
    item.setId(row.getApplicationId());
    item.setProgramId(row.getId());
    item.setStatusCode(row.getStatusCode());
    item.setNoteText(row.getNoteText());
    item.setProgram(toProgramListItem(row));
    item.setMatchResult(computeAndPersistMatch(userId, row));
    return item;
  }

  private UniversityDtos.ProgramMatchResult computeAndPersistMatch(Long userId, Program program) {
    StudentProfile profile = studentProfileMapper.findByUserId(userId);
    List<StudentLanguageScore> languageScores = studentLanguageScoreMapper.listByUserId(userId);
    List<StudentTargetCountry> targetCountries = studentTargetCountryMapper.listByUserId(userId);

    int score = 45;
    List<String> reasons = new ArrayList<>();

    if (targetCountries.stream().anyMatch(it -> safeEquals(it.getCountryName(), program.getCountryName()))) {
      score += 10;
      reasons.add("命中目标国家");
    } else {
      reasons.add("国家偏好不完全命中");
    }

    if (profile != null && containsIgnoreCase(profile.getTargetMajorText(), program.getDirectionName())) {
      score += 15;
      reasons.add("命中目标专业方向");
    } else {
      reasons.add("专业方向建议进一步确认");
    }

    if (profile != null && profile.getGpaValue() != null && program.getGpaMinRecommend() != null) {
      BigDecimal diff = profile.getGpaValue().subtract(program.getGpaMinRecommend());
      if (diff.compareTo(new BigDecimal("0.30")) >= 0) {
        score += 14;
        reasons.add("GPA竞争力较强");
      } else if (diff.compareTo(BigDecimal.ZERO) >= 0) {
        score += 10;
        reasons.add("GPA达到建议线");
      } else if (diff.compareTo(new BigDecimal("-0.30")) >= 0) {
        score += 6;
        reasons.add("GPA略低于建议线");
      } else {
        reasons.add("GPA需重点提升");
      }
    } else {
      reasons.add("缺少完整GPA信息");
    }

    if (program.getLanguageType() != null && program.getLanguageMinScore() != null) {
      StudentLanguageScore bestScore = languageScores.stream()
        .filter(it -> safeEquals(it.getLanguageType(), program.getLanguageType()))
        .findFirst()
        .orElse(null);

      if (bestScore == null || bestScore.getScore() == null) {
        reasons.add("缺少对应语言成绩");
      } else {
        BigDecimal diff = bestScore.getScore().subtract(program.getLanguageMinScore());
        if (diff.compareTo(new BigDecimal("0.50")) >= 0) {
          score += 10;
          reasons.add("语言成绩有优势");
        } else if (diff.compareTo(BigDecimal.ZERO) >= 0) {
          score += 8;
          reasons.add("语言成绩达标");
        } else if (diff.compareTo(new BigDecimal("-0.50")) >= 0) {
          score += 4;
          reasons.add("语言成绩接近门槛");
        } else {
          reasons.add("语言成绩需提升");
        }
      }
    }

    if (profile != null && profile.getBudgetMax() != null && program.getTuitionAmount() != null) {
      if (profile.getBudgetMax().compareTo(program.getTuitionAmount()) >= 0) {
        score += 6;
        reasons.add("预算覆盖学费");
      } else {
        score -= 4;
        reasons.add("预算可能不足");
      }
    }

    if (Boolean.TRUE.equals(program.getRequiresGre()) || Boolean.TRUE.equals(program.getRequiresGmat())) {
      score -= 2;
      reasons.add("标准化考试要求较高");
    }

    if (score < 0) score = 0;
    if (score > 100) score = 100;

    String tier;
    if (score >= 80) {
      tier = "保底校";
    } else if (score >= 65) {
      tier = "匹配校";
    } else {
      tier = "冲刺校";
    }

    String reasonJson;
    try {
      reasonJson = objectMapper.writeValueAsString(reasons);
    } catch (Exception ex) {
      reasonJson = "[]";
    }

    studentProgramMatchResultMapper.upsert(userId, program.getId(), score, tier, reasonJson);

    UniversityDtos.ProgramMatchResult result = new UniversityDtos.ProgramMatchResult();
    result.setMatchScore(score);
    result.setMatchTier(tier);
    result.setReasonTags(reasons);
    return result;
  }

  private UniversityDtos.FilterOption buildOption(String code, String name) {
    UniversityDtos.FilterOption option = new UniversityDtos.FilterOption();
    option.setCode(code);
    option.setName(name);
    return option;
  }

  private boolean safeEquals(String left, String right) {
    return left != null && right != null && left.equalsIgnoreCase(right);
  }

  private boolean containsIgnoreCase(String src, String part) {
    if (src == null || part == null) return false;
    return src.toLowerCase().contains(part.toLowerCase());
  }

  private String normalizeKeyword(String keyword) {
    if (keyword == null) return null;
    String value = keyword.trim();
    return value.isEmpty() ? null : value;
  }
}
