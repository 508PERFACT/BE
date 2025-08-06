package com.perfact.be.domain.auth.exception.status;

import com.perfact.be.global.apiPayload.code.BaseCode;
import com.perfact.be.global.apiPayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessStatus implements BaseCode {

  SOCIAL_LOGIN_SUCCESS(HttpStatus.OK, "AUTH2001", "소셜 로그인이 완료되었습니다."),
  DEV_TOKEN_ISSUED(HttpStatus.OK, "AUTH2002", "개발용 액세스 토큰 발급이 완료되었습니다."),
  LOGOUT_SUCCESS(HttpStatus.OK, "AUTH2003", "로그아웃이 완료되었습니다."),
  AT_REFRESH_SUCCESS(HttpStatus.OK, "AUTH2004", "엑세스 토큰 재생성이 완료되었습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ReasonDto getReason() {
    return ReasonDto.builder()
        .isSuccess(true)
        .code(code)
        .message(message)
        .build();
  }

  @Override
  public ReasonDto getReasonHttpStatus() {
    return ReasonDto.builder()
        .httpStatus(httpStatus)
        .isSuccess(true)
        .code(code)
        .message(message)
        .build();
  }
}
