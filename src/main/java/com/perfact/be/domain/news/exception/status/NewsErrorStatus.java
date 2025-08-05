package com.perfact.be.domain.news.exception.status;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NewsErrorStatus implements BaseErrorCode {
  NOT_NAVER_NEWS(HttpStatus.BAD_REQUEST, "NEWS4001", "네이버 뉴스 도메인이 아닙니다. 네이버 뉴스를 통한 링크만 가능합니다."),
  NEWS_CONTENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "NEWS4002", "뉴스 내용을 찾을 수 없습니다."),
  NEWS_TITLE_EXTRACTION_FAILED(HttpStatus.BAD_REQUEST, "NEWS4003", "뉴스 제목 추출에 실패했습니다."),
  NEWS_DATE_EXTRACTION_FAILED(HttpStatus.BAD_REQUEST, "NEWS4004", "뉴스 날짜 추출에 실패했습니다."),
  NEWS_ARTICLE_PARSING_FAILED(HttpStatus.BAD_REQUEST, "NEWS4005", "뉴스 기사 파싱에 실패했습니다."),
  NEWS_NAVER_API_CALL_FAILED(HttpStatus.BAD_REQUEST, "NEWS4006", "네이버 API 호출에 실패했습니다."),
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