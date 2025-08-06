package com.perfact.be.domain.news.exception;

import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewsExceptionHandler {

  // 뉴스 파싱 실패 예외 처리
  public void handleParsingFailure(String url, String operation, Exception e) {
    log.error("Failed to {} for URL: {}", operation, url, e);
    throw new NewsHandler(NewsErrorStatus.NEWS_ARTICLE_PARSING_FAILED);
  }

  // 뉴스 내용 찾을 수 없을 때 예외 처리
  public void handleContentNotFound(String url, String operation) {
    log.warn("Content not found during {} for URL: {}", operation, url);
    throw new NewsHandler(NewsErrorStatus.NEWS_CONTENT_NOT_FOUND);
  }

  // 뉴스 제목 추출 실패 예외 처리
  public void handleTitleExtractionFailure(String url, String operation, Exception e) {
    log.error("Failed to extract title during {} for URL: {}", operation, url, e);
    throw new NewsHandler(NewsErrorStatus.NEWS_TITLE_EXTRACTION_FAILED);
  }

  // 네이버 API 호출 실패 예외 처리
  public void handleNaverApiFailure(String query, Exception e) {
    log.error("Failed to call Naver API for query: {}", query, e);
    throw new NewsHandler(NewsErrorStatus.NEWS_NAVER_API_CALL_FAILED);
  }

  // 안전한 텍스트 추출 수행 실패 시 null 반환
  public String safeExtractText(String url, String operation, TextExtractor extractor) {
    try {
      return extractor.extract();
    } catch (Exception e) {
      log.warn("Text extraction failed during {} for URL: {}", operation, url, e);
      return null;
    }
  }

  @FunctionalInterface
  public interface TextExtractor {
    String extract() throws Exception;
  }
}