package com.perfact.be.domain.auth.exception.status;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements BaseErrorCode {
  NAVER_TOKEN_REQUEST_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_4001", "네이버 Access Token 요청 실패"),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RT_4001","유효하지 않은 리프레시 토큰입니다."),
  NAVER_USERINFO_REQUEST_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_4002", "네이버 사용자 정보 요청 실패"),
  USER_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH_4003","해당 토큰에 대한 접근 권한이 없습니다." );


  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ErrorReasonDto getReason() {
    return ErrorReasonDto.builder()
        .isSuccess(false)
        .message(message)
        .code(code)
        .build();
  }

  @Override
  public ErrorReasonDto getReasonHttpStatus() {
    return ErrorReasonDto.builder()
        .httpStatus(httpStatus)
        .isSuccess(false)
        .code(code)
        .message(message)
        .build();
  }
}
