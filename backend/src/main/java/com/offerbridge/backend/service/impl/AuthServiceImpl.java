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
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.security.JwtService;
import com.offerbridge.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
  private static final String SCENE_LOGIN_REGISTER = "LOGIN_REGISTER";
  private static final String SCENE_ADMIN_LOGIN = "ADMIN_LOGIN";
  private static final String LOGIN_METHOD_SMS_CODE = "SMS_CODE";
  private static final String LOGIN_METHOD_PASSWORD = "PASSWORD";
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
  private final PasswordEncoder passwordEncoder;

  public AuthServiceImpl(AppProperties appProperties,
                         StringRedisTemplate redisTemplate,
                         AuthSmsCodeMapper authSmsCodeMapper,
                         UserAccountMapper userAccountMapper,
                         StudentProfileMapper studentProfileMapper,
                         StudentLanguageScoreMapper studentLanguageScoreMapper,
                         VerificationRecordMapper verificationRecordMapper,
                         AuthRefreshTokenMapper authRefreshTokenMapper,
                         LoginAuditLogMapper loginAuditLogMapper,
                         JwtService jwtService,
                         PasswordEncoder passwordEncoder) {
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
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public AuthDtos.SendSmsResult sendSmsCode(AuthDtos.SendSmsRequest request) {
    String scene = normalizeScene(request.getScene());
    String dbScene = mapToDbScene(scene);
    String rateKey = "sms:rate:" + scene + ":" + request.getPhone();
    Boolean hasKey = redisTemplate.hasKey(rateKey);
    if (Boolean.TRUE.equals(hasKey)) {
      throw new BizException("BIZ_SMS_TOO_FREQUENT", "发送过于频繁，请稍后再试");
    }

    String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(appProperties.getSms().getCodeTtlSeconds());
    authSmsCodeMapper.insertCode(request.getPhone(), dbScene, sha256(code), expiresAt);

    redisTemplate.opsForValue().set(rateKey, "1", appProperties.getSms().getSendIntervalSeconds(), TimeUnit.SECONDS);

    AuthDtos.SendSmsResult result = new AuthDtos.SendSmsResult();
    if (appProperties.getSms().isMockEnabled()) {
      System.out.println("[MOCK_SMS] phone=" + request.getPhone() + ", scene=" + scene + ", code=" + code);
      result.setMockCode(code);
    }
    return result;
  }

  @Override
  @Transactional
  public AuthDtos.AuthResult smsLoginOrRegister(AuthDtos.SmsLoginRequest request, HttpServletRequest httpRequest) {
    verifyCode(request.getPhone(), mapToDbScene(SCENE_LOGIN_REGISTER), request.getCode());
    String requestRole = request.getRole();

    UserAccount user = userAccountMapper.findByPhone(request.getPhone());
    if (user == null) {
      String registerRole = normalizeRegistrationRole(requestRole);
      insertUserCompat(request.getPhone(), registerRole);
      user = userAccountMapper.findByPhone(request.getPhone());
    }
    ensureSupportedAndActive(user);

    String effectiveRole = resolveLoginRole(requestRole, user);
    if (!isCompatibleLoginRole(effectiveRole, user.getRole())) {
      log.warn("Login role mismatch phone={}, requestRole={}, effectiveRole={}, actualRole={}",
        request.getPhone(), requestRole, effectiveRole, user.getRole());
      throw new BizException("BIZ_FORBIDDEN", "当前手机号已注册为其他角色");
    }
    if ("STUDENT".equals(user.getRole()) && studentProfileMapper.findByUserId(user.getId()) == null) {
      studentProfileMapper.insertEmpty(user.getId());
    }

    userAccountMapper.updateLastLoginAt(user.getId());
    logLogin(user.getId(), user.getPhone(), LOGIN_METHOD_SMS_CODE, true, null, httpRequest);
    return buildAuthResult(user, httpRequest);
  }

  @Override
  @Transactional
  public AuthDtos.AuthResult passwordLogin(AuthDtos.PasswordLoginRequest request, HttpServletRequest httpRequest) {
    UserAccount user = userAccountMapper.findByPhone(request.getPhone());
    if (user == null) {
      logPasswordFailure(null, request.getPhone(), "USER_NOT_FOUND", httpRequest);
      throw new BizException("BIZ_UNAUTHORIZED", "手机号或密码错误");
    }

    String effectiveRole;
    try {
      ensureSupportedAndActive(user);
      effectiveRole = resolveLoginRole(request.getRole(), user);
      if (!isCompatibleLoginRole(effectiveRole, user.getRole())) {
        logPasswordFailure(user.getId(), user.getPhone(), "ROLE_MISMATCH", httpRequest);
        throw new BizException("BIZ_FORBIDDEN", "当前手机号已注册为其他角色");
      }
    } catch (BizException ex) {
      if (!"BIZ_FORBIDDEN".equals(ex.getCode())) {
        logPasswordFailure(user.getId(), user.getPhone(), ex.getCode(), httpRequest);
      }
      throw ex;
    }

    if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
      logPasswordFailure(user.getId(), user.getPhone(), "PASSWORD_NOT_SET", httpRequest);
      throw new BizException("BIZ_PASSWORD_NOT_SET", "该账号尚未设置密码，请先使用验证码登录后在个人中心设置密码");
    }
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      logPasswordFailure(user.getId(), user.getPhone(), "BAD_CREDENTIALS", httpRequest);
      throw new BizException("BIZ_UNAUTHORIZED", "手机号或密码错误");
    }

    if ("STUDENT".equals(user.getRole()) && studentProfileMapper.findByUserId(user.getId()) == null) {
      studentProfileMapper.insertEmpty(user.getId());
    }
    userAccountMapper.updateLastLoginAt(user.getId());
    logLogin(user.getId(), user.getPhone(), LOGIN_METHOD_PASSWORD, true, null, httpRequest);
    return buildAuthResult(user, httpRequest);
  }

  @Override
  @Transactional
  public AuthDtos.PasswordStatusResult passwordStatus() {
    Long userId = AuthContext.getUserId();
    if (userId == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "未登录或登录已过期");
    }
    UserAccount user = requireUser(userId);
    AuthDtos.PasswordStatusResult result = new AuthDtos.PasswordStatusResult();
    result.setHasPassword(user.getPasswordHash() != null && !user.getPasswordHash().isBlank());
    return result;
  }

  @Override
  @Transactional
  public void updatePassword(AuthDtos.UpdatePasswordRequest request) {
    Long userId = AuthContext.getUserId();
    if (userId == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "未登录或登录已过期");
    }
    UserAccount user = requireUser(userId);
    ensureSupportedAndActive(user);

    boolean hasPassword = user.getPasswordHash() != null && !user.getPasswordHash().isBlank();
    if (hasPassword) {
      String currentPassword = request.getCurrentPassword();
      if (currentPassword == null || currentPassword.isBlank()) {
        throw new BizException("BIZ_BAD_REQUEST", "请输入当前密码");
      }
      if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
        throw new BizException("BIZ_UNAUTHORIZED", "当前密码错误");
      }
    }

    if (hasPassword && passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
      throw new BizException("BIZ_BAD_REQUEST", "新密码不能与当前密码相同");
    }
    userAccountMapper.updatePasswordHash(user.getId(), passwordEncoder.encode(request.getNewPassword()));
  }

  @Override
  @Transactional
  public AuthDtos.AuthResult adminSmsLogin(AuthDtos.AdminSmsLoginRequest request, HttpServletRequest httpRequest) {
    verifyCode(request.getPhone(), mapToDbScene(SCENE_ADMIN_LOGIN), request.getCode());
    UserAccount user = userAccountMapper.findByPhone(request.getPhone());
    if (user == null || !"ADMIN".equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "当前手机号不是管理员账号");
    }
    ensureSupportedAndActive(user);
    userAccountMapper.updateLastLoginAt(user.getId());
    // Keep db-compatible method value; admin identity can be distinguished by user role.
    logLogin(user.getId(), user.getPhone(), LOGIN_METHOD_SMS_CODE, true, null, httpRequest);
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
    result.setHasPassword(user.getPasswordHash() != null && !user.getPasswordHash().isBlank());
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
    if (!"STUDENT".equals(user.getRole()) && !"AGENT_ORG".equals(user.getRole()) && !"AGENT_MEMBER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
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

  private String normalizeRegistrationRole(String role) {
    if ("STUDENT".equals(role) || "AGENT_ORG".equals(role) || "AGENT_MEMBER".equals(role)) {
      return role;
    }
    throw new BizException("BIZ_BAD_REQUEST", "缺少角色参数，无法注册新账号");
  }

  private String resolveLoginRole(String requestRole, UserAccount user) {
    if (requestRole != null && !requestRole.isBlank()) {
      if ("STUDENT".equals(requestRole) || "AGENT_ORG".equals(requestRole) || "AGENT_MEMBER".equals(requestRole)) {
        return requestRole;
      }
      throw new BizException("BIZ_BAD_REQUEST", "role 参数不合法");
    }
    // Backward compatibility for old clients that may miss role in payload.
    if ("STUDENT".equals(user.getRole())) {
      return "STUDENT";
    }
    if ("AGENT_ORG".equals(user.getRole()) || "AGENT_MEMBER".equals(user.getRole())) {
      return "AGENT_ORG";
    }
    throw new BizException("BIZ_FORBIDDEN", "当前账号角色不可用");
  }

  private boolean isCompatibleLoginRole(String requestRole, String actualRole) {
    if (requestRole == null || actualRole == null) {
      return false;
    }
    if (requestRole.equals(actualRole)) {
      return true;
    }
    // The login page now has only STUDENT / AGENT entrances.
    // AGENT entrance sends AGENT_ORG, but AGENT_MEMBER should also be able to log in.
    return "AGENT_ORG".equals(requestRole) && "AGENT_MEMBER".equals(actualRole);
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
    if (!LOGIN_METHOD_SMS_CODE.equals(method) && !LOGIN_METHOD_PASSWORD.equals(method)) {
      throw new BizException("BIZ_BAD_REQUEST", "登录方式不合法");
    }
    loginAuditLogMapper.insertLog(userId, phone, method, success, reason, getIp(req), getUserAgent(req));
  }

  private void logPasswordFailure(Long userId, String phone, String reason, HttpServletRequest req) {
    try {
      logLogin(userId, phone, LOGIN_METHOD_PASSWORD, false, reason, req);
    } catch (Exception ex) {
      log.warn("Failed to write password login audit phone={}, reason={}", phone, reason, ex);
    }
  }

  private String normalizeScene(String scene) {
    if (SCENE_LOGIN_REGISTER.equals(scene) || SCENE_ADMIN_LOGIN.equals(scene)) {
      return scene;
    }
    throw new BizException("BIZ_BAD_REQUEST", "scene 参数不合法");
  }

  private String mapToDbScene(String scene) {
    // Compatibility: some deployed schemas have strict scene constraints and do not include ADMIN_LOGIN.
    // Use LOGIN_REGISTER as stored scene to keep admin SMS flow available before schema migration.
    if (SCENE_ADMIN_LOGIN.equals(scene)) {
      return SCENE_LOGIN_REGISTER;
    }
    return scene;
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
