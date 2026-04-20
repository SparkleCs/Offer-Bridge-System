package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.AuthDtos;
import com.offerbridge.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/sms/send")
  public ApiResponse<AuthDtos.SendSmsResult> sendSms(@Valid @RequestBody AuthDtos.SendSmsRequest request) {
    return ApiResponse.ok(authService.sendSmsCode(request));
  }

  @PostMapping("/sms/login-or-register")
  public ApiResponse<AuthDtos.AuthResult> smsLoginOrRegister(@Valid @RequestBody AuthDtos.SmsLoginRequest request,
                                                             HttpServletRequest httpRequest) {
    return ApiResponse.ok(authService.smsLoginOrRegister(request, httpRequest));
  }

  @PostMapping("/refresh")
  public ApiResponse<AuthDtos.AuthResult> refresh(@Valid @RequestBody AuthDtos.RefreshRequest request,
                                                   HttpServletRequest httpRequest) {
    return ApiResponse.ok(authService.refresh(request, httpRequest));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(@Valid @RequestBody AuthDtos.RefreshRequest request) {
    authService.logout(request);
    return ApiResponse.ok();
  }
}
