package com.perfact.be.domain.user.exception.status;

import com.perfact.be.global.apiPayload.code.BaseCode;
import com.perfact.be.global.apiPayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessStatus implements BaseCode {

  SOCIAL_LOGIN_SUCCESS(HttpStatus.OK, "AUTH2001", "소셜 로그인이 완료되었습니다."),

  ;

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
