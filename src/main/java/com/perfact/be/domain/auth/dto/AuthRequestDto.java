package com.perfact.be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequestDto {
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "로그아웃 및 액세스 토큰 재발급 요청 DTO")
  public static class RefreshTokenRequest {
    @NotBlank
    @Schema(
        description = "JWT 리프레시 토큰",
        example = "easdasdadasdasdasdasdasd..."
    )
    private String refreshToken;
  }

}
