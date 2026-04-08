package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.config.AppProperties;
import com.offerbridge.backend.dto.AuthDtos;
import com.offerbridge.backend.entity.AuthRefreshToken;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.entity.VerificationRecord;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AuthRefreshTokenMapper;
import com.offerbridge.backend.mapper.AuthSmsCodeMapper;
import com.offerbridge.backend.mapper.LoginAuditLogMapper;
import com.offerbridge.backend.mapper.StudentLanguageScoreMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.mapper.VerificationRecordMapper;
import com.offerbridge.backend.security.JwtService;
import com.offerbridge.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
  private final AppProperties appProperties;
  private final StringRedisTemplate redisTemplate;
  private final AuthSmsCodeMapper authSmsCodeMapper;
  private final UserAccountMapper userAccountMapper;
  private final StudentProfileMapper studentProfileMapper;
  private final StudentLanguageScoreMapper studentLanguageScoreMapper;
  private final VerificationRecordMapper verificationRecordMapper;
  private final AuthRefreshTokenMapper authRefreshTokenMapper;
  private final LoginAuditLogMapper loginAuditLogMapper;
  private final JwtService jwtService;

  public AuthServiceImpl(AppProperties appProperties,
                         StringRedisTemplate redisTemplate,
                         AuthSmsCodeMapper authSmsCodeMapper,
                         UserAccountMapper userAccountMapper,
                         StudentProfileMapper studentProfileMapper,
                         StudentLanguageScoreMapper studentLanguageScoreMapper,
                         VerificationRecordMapper verificationRecordMapper,
                         AuthRefreshTokenMapper authRefreshTokenMapper,
                         LoginAuditLogMapper loginAuditLogMapper,
                         JwtService jwtService) {
    this.appProperties = appProperties;
    this.redisTemplate = redisTemplate;
    this.authSmsCodeMapper = authSmsCodeMapper;
    this.userAccountMapper = userAccountMapper;
    this.studentProfileMapper = studentProfileMapper;
    this.studentLanguageScoreMapper = studentLanguageScoreMapper;
    this.verificationRecordMapper = verificationRecordMapper;
    this.authRefreshTokenMapper = authRefreshTokenMapper;
    this.loginAuditLogMapper = loginAuditLogMapper;
    this.jwtService = jwtService;
  }

  @Override
  public void sendSmsCode(AuthDtos.SendSmsRequest request) {
    String scene = normalizeScene(request.getScene());
    String rateKey = "sms:rate:" + scene + ":" + request.getPhone();
    Boolean hasKey = redisTemplate.hasKey(rateKey);
    if (Boolean.TRUE.equals(hasKey)) {
      throw new BizException("BIZ_SMS_TOO_FREQUENT", "发送过于频繁，请稍后再试");
    }

    String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(appProperties.getSms().getCodeTtlSeconds());
    authSmsCodeMapper.insertCode(request.getPhone(), scene, sha256(code), expiresAt);

    redisTemplate.opsForValue().set(rateKey, "1", appProperties.getSms().getSendIntervalSeconds(), TimeUnit.SECONDS);

    if (appProperties.getSms().isMockEnabled()) {
      System.out.println("[MOCK_SMS] phone=" + request.getPhone() + ", scene=" + scene + ", code=" + code);
    }
  }

  @Override
  @Transactional
  public AuthDtos.AuthResult smsLoginOrRegister(AuthDtos.SmsLoginRequest request, HttpServletRequest httpRequest) {
    verifyCode(request.getPhone(), "LOGIN_REGISTER", request.getCode());
    String role = normalizeLoginRole(request.getRole());

    UserAccount user = userAccountMapper.findByPhone(request.getPhone());
    if (user == null) {
      insertUserCompat(request.getPhone(), role);
      user = userAccountMapper.findByPhone(request.getPhone());
    }
    ensureSupportedAndActive(user);
    if (!role.equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "当前手机号已注册为其他角色");
    }
    if ("STUDENT".equals(user.getRole()) && studentProfileMapper.findByUserId(user.getId()) == null) {
      studentProfileMapper.insertEmpty(user.getId());
    }

    userAccountMapper.updateLastLoginAt(user.getId());
    logLogin(user.getId(), user.getPhone(), "SMS_CODE", true, null, httpRequest);
    return buildAuthResult(user, httpRequest);
  }

  @Override
  @Transactional
  public AuthDtos.AuthResult refresh(AuthDtos.RefreshRequest request, HttpServletRequest httpRequest) {
    String refreshToken = request.getRefreshToken();
    String tokenHash = sha256(refreshToken);
    AuthRefreshToken dbToken = authRefreshTokenMapper.findValidToken(tokenHash);
    if (dbToken == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "刷新令牌无效");
    }

    Long userId;
    try {
      userId = jwtService.parseUserId(refreshToken);
    } catch (Exception ex) {
      throw new BizException("BIZ_UNAUTHORIZED", "刷新令牌无效");
    }

    if (!dbToken.getUserId().equals(userId)) {
      throw new BizException("BIZ_UNAUTHORIZED", "刷新令牌无效");
    }

    authRefreshTokenMapper.revokeToken(tokenHash);
    UserAccount user = requireUser(userId);
    return buildAuthResult(user, httpRequest);
  }

  @Override
  public void logout(AuthDtos.RefreshRequest request) {
    authRefreshTokenMapper.revokeToken(sha256(request.getRefreshToken()));
  }

  private AuthDtos.AuthResult buildAuthResult(UserAccount user, HttpServletRequest httpRequest) {
    String accessToken = jwtService.issueAccessToken(user.getId());
    String refreshToken = jwtService.issueRefreshToken(user.getId());

    Instant refreshExpire = jwtService.parseExpiry(refreshToken);
    authRefreshTokenMapper.insertToken(
      user.getId(),
      sha256(refreshToken),
      "web",
      getIp(httpRequest),
      getUserAgent(httpRequest),
      LocalDateTime.ofInstant(refreshExpire, ZoneId.systemDefault())
    );

    StudentProfile profile = studentProfileMapper.findByUserId(user.getId());
    boolean profileCompleted = isProfileCompleted(profile);
    boolean verificationCompleted = isVerificationCompleted(user.getId());

    AuthDtos.AuthResult result = new AuthDtos.AuthResult();
    result.setAccessToken(accessToken);
    result.setRefreshToken(refreshToken);
    result.setUserId(user.getId());
    result.setRole(user.getRole());
    result.setProfileCompleted(profileCompleted);
    result.setVerificationCompleted(verificationCompleted);
    return result;
  }

  private void verifyCode(String phone, String scene, String inputCode) {
    int anyValidCount = authSmsCodeMapper.countAnyValidCode(phone, scene);
    if (anyValidCount <= 0) {
      throw new BizException("BIZ_SMS_CODE_EXPIRED", "验证码已过期，请重新发送");
    }

    String codeHash = sha256(inputCode);
    int count = authSmsCodeMapper.countValidCode(phone, scene, codeHash);
    if (count <= 0) {
      authSmsCodeMapper.increaseAttempt(phone, scene);
      throw new BizException("BIZ_SMS_CODE_INVALID", "验证码错误");
    }
    authSmsCodeMapper.markUsedByCode(phone, scene, codeHash);
  }

  private void ensureSupportedAndActive(UserAccount user) {
    if (!"STUDENT".equals(user.getRole()) && !"AGENT_ORG".equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "当前账号角色不可用");
    }
    if (!"ACTIVE".equals(user.getStatus())) {
      throw new BizException("BIZ_ACCOUNT_DISABLED", "账号不可用");
    }
  }

  private UserAccount requireUser(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "用户不存在");
    }
    ensureSupportedAndActive(user);
    return user;
  }

  private String normalizeLoginRole(String role) {
    if ("STUDENT".equals(role) || "AGENT_ORG".equals(role)) {
      return role;
    }
    throw new BizException("BIZ_BAD_REQUEST", "role 参数不合法");
  }

  private boolean isProfileCompleted(StudentProfile profile) {
    if (profile == null) return false;
    return notBlank(profile.getName())
      && notBlank(profile.getEducationLevel())
      && notBlank(profile.getSchoolName())
      && notBlank(profile.getMajor())
      && profile.getGpaValue() != null
      && notBlank(profile.getGpaScale())
      && !studentLanguageScoreMapper.listByUserId(profile.getUserId()).isEmpty();
  }

  private boolean isVerificationCompleted(Long userId) {
    VerificationRecord realName = verificationRecordMapper.findOne(userId, "REAL_NAME");
    VerificationRecord education = verificationRecordMapper.findOne(userId, "EDUCATION");
    return realName != null && education != null
      && "APPROVED".equals(realName.getStatus())
      && "APPROVED".equals(education.getStatus());
  }

  private void logLogin(Long userId, String phone, String method, boolean success, String reason, HttpServletRequest req) {
    loginAuditLogMapper.insertLog(userId, phone, method, success, reason, getIp(req), getUserAgent(req));
  }

  private String normalizeScene(String scene) {
    if ("LOGIN_REGISTER".equals(scene)) {
      return scene;
    }
    throw new BizException("BIZ_BAD_REQUEST", "scene 参数不合法");
  }

  private String getIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    return (ip == null || ip.isBlank()) ? request.getRemoteAddr() : ip;
  }

  private String getUserAgent(HttpServletRequest request) {
    return request.getHeader("User-Agent");
  }

  private boolean notBlank(String value) {
    return value != null && !value.isBlank();
  }

  private String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (Exception ex) {
      throw new BizException("BIZ_INTERNAL_ERROR", "系统异常");
    }
  }

  private void insertUserCompat(String phone, String role) {
    try {
      userAccountMapper.insertByRole(phone, role);
    } catch (Exception ex) {
      // Compatible with legacy schemas where password_hash still exists and may be NOT NULL.
      String message = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
      if (message.contains("password_hash") || message.contains("doesn't have a default value") || message.contains("cannot be null")) {
        userAccountMapper.insertByRoleWithEmptyPassword(phone, role);
        return;
      }
      throw ex;
    }
  }
}
