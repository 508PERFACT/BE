package com.perfact.be.domain.alt.exception.status;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AltErrorStatus implements BaseErrorCode {
  ALT_NOT_FOUND(HttpStatus.NOT_FOUND, "ALT4001", "대안 기사를 찾을 수 없습니다."),
  ALT_SEARCH_FAILED(HttpStatus.BAD_REQUEST, "ALT4002", "네이버 뉴스 검색에 실패했습니다."),
  ALT_EXTRACTION_FAILED(HttpStatus.BAD_REQUEST, "ALT4003", "기사 본문 추출에 실패했습니다."),
  ALT_COMPARISON_FAILED(HttpStatus.BAD_REQUEST, "ALT4004", "비교 분석에 실패했습니다."),
  ALT_CONVERSION_FAILED(HttpStatus.BAD_REQUEST, "ALT4005", "데이터 변환에 실패했습니다."),
  ALT_PERSISTENCE_FAILED(HttpStatus.BAD_REQUEST, "ALT4006", "데이터 저장에 실패했습니다."),
  ALT_CLOVA_API_FAILED(HttpStatus.BAD_REQUEST, "ALT4007", "Clova API 호출에 실패했습니다."),
  ALT_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "ALT4008", "잘못된 요청입니다."),
  ALT_NO_SEARCH_RESULTS(HttpStatus.NOT_FOUND, "ALT4009", "네이버 뉴스 검색 결과가 없습니다."),
  ALT_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "ALT4010", "해당 리포트를 찾을 수 없습니다.");

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
