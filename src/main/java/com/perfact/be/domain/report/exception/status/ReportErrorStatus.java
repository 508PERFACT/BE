package com.perfact.be.domain.report.exception.status;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorStatus implements BaseErrorCode {
  REPORT_CREATION_FAILED(HttpStatus.BAD_REQUEST, "REPORT4001", "리포트 생성에 실패했습니다."),
  CLOVA_API_CALL_FAILED(HttpStatus.BAD_REQUEST, "REPORT4002", "Clova API 호출에 실패했습니다."),
  ANALYSIS_RESULT_PARSING_FAILED(HttpStatus.BAD_REQUEST, "REPORT4003", "분석 결과 파싱에 실패했습니다."),
  REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4004", "리포트를 찾을 수 없습니다."),
  REPORT_CONVERSION_FAILED(HttpStatus.BAD_REQUEST, "REPORT4005", "리포트 변환에 실패했습니다."),
  ;

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