package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.AuthDtos;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
  AuthDtos.SendSmsResult sendSmsCode(AuthDtos.SendSmsRequest request);
  AuthDtos.AuthResult smsLoginOrRegister(AuthDtos.SmsLoginRequest request, HttpServletRequest httpRequest);
  AuthDtos.AuthResult adminSmsLogin(AuthDtos.AdminSmsLoginRequest request, HttpServletRequest httpRequest);
  AuthDtos.AuthResult refresh(AuthDtos.RefreshRequest request, HttpServletRequest httpRequest);
  void logout(AuthDtos.RefreshRequest request);
}
