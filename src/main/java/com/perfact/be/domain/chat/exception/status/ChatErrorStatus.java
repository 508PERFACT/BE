package com.perfact.be.domain.chat.exception.status;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorStatus implements BaseErrorCode {

  CHAT_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT4001", "채팅 API 호출에 실패했습니다."),
  CHAT_MESSAGE_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT4002", "채팅 메시지 파싱에 실패했습니다."),
  CHAT_LOG_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT4003", "채팅 로그 저장에 실패했습니다."),
  CHAT_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4004", "해당 리포트를 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ErrorReasonDto getReason() {
    return ErrorReasonDto.builder()
        .isSuccess(false)
        .code(this.code)
        .message(this.message)
        .build();
  }

  @Override
  public ErrorReasonDto getReasonHttpStatus() {
    return ErrorReasonDto.builder()
        .isSuccess(false)
        .code(this.code)
        .message(this.message)
        .httpStatus(this.httpStatus)
        .build();
  }
}