package com.perfact.be.domain.news.converter;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * News 도메인의 데이터 변환을 담당하는 Converter
 */
@Slf4j
@Component
public class NewsConverter {

  /**
   * 뉴스 데이터를 NewsArticleResponse로 변환
   */
  public NewsArticleResponse toNewsArticleResponse(String title, String date, String content) {
    try {
      return new NewsArticleResponse(title, date, content);
    } catch (Exception e) {
      throw new RuntimeException("NewsArticleResponse 변환 실패", e);
    }
  }

  /**
   * 뉴스 데이터를 JSON 형태로 변환
   */
  public String toJsonString(NewsArticleResponse newsData) {
    try {
      return String.format(
          "{\"title\": \"%s\", \"date\": \"%s\", \"content\": \"%s\"}",
          escapeJsonString(newsData.getTitle()),
          escapeJsonString(newsData.getDate()),
          escapeJsonString(newsData.getContent()));
    } catch (Exception e) {
      throw new RuntimeException("JSON 변환 실패", e);
    }
  }

  /**
   * JSON 문자열에서 특수문자 이스케이프 처리
   */
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