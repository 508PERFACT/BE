package com.perfact.be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class AuthResponseDto {
  @Getter
  @AllArgsConstructor
  @Schema(description = "로그인 응답 DTO")
  public static class LoginResponse {

    @Schema(description = "유저의 고유 ID", example = "1")
    private Long userId;

    @Schema(description = "유저 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;
  }

}
