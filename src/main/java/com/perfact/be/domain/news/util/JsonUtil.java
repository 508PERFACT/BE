package com.perfact.be.domain.news.util;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// JSON 관련 유틸리티 클래스
@Slf4j
@Component
public class JsonUtil {

  // NewsArticleResponse를 JSON 문자열로 변환
  public String toJsonString(NewsArticleResponse newsData) {
    try {
      return String.format(
          "{\"title\": \"%s\", \"date\": \"%s\", \"content\": \"%s\"}",
          escapeJsonString(newsData.getTitle()),
          escapeJsonString(newsData.getDate()),
          escapeJsonString(newsData.getContent()));
    } catch (Exception e) {
      log.error("JSON 변환 실패: {}", e.getMessage(), e);
      throw new RuntimeException("JSON 변환 실패", e);
    }
  }

  // JSON 문자열에서 특수 문자를 이스케이프 처리
  private String escapeJsonString(String input) {
    if (input == null) {
      return "";
    }
    return input.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }
}