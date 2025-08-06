package com.perfact.be.domain.auth.service;

import com.perfact.be.domain.auth.dto.AuthResponseDto;
import com.perfact.be.domain.auth.dto.AuthResponseDto.TokenResponse;
import com.perfact.be.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;

public interface AuthService {

  AuthResponseDto.LoginResponse socialLogin(String code, String state);

  TokenResponse refreshAccessToken(User loginUser, String refreshToken);

  void logout(User loginUser, String refreshToken);
}
