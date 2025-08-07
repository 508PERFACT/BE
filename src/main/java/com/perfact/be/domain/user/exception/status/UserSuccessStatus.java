package com.perfact.be.domain.user.exception.status;

import com.perfact.be.global.apiPayload.code.BaseCode;
import com.perfact.be.global.apiPayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessStatus implements BaseCode {

  SOCIAL_LOGIN_SUCCESS(HttpStatus.OK, "USER2001", "소셜 로그인이 완료되었습니다."),
  GET_SUBSCRIBE_STATUS_SUCCESS(HttpStatus.OK, "USER2002", "구독 상태 조회 성공"),
  SUBSCRIBE_SUCCESS(HttpStatus.OK, "USER2003", "구독 신청 성공"),
  UNSUBSCRIBE_SUCCESS(HttpStatus.OK, "USER2004", "구독 해지 성공"),
  GET_REPORT_LIST_SUCCESS(HttpStatus.OK, "USER2005","과거 레포트 리스트 조회 성공")

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
