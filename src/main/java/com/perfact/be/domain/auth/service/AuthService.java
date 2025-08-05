package com.perfact.be.domain.auth.service;

import com.perfact.be.domain.auth.dto.AuthResponseDto;

public interface AuthService {

  AuthResponseDto.LoginResponse socialLogin(String code, String state);
}
