package com.perfact.be.domain.news.converter;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewsConverter {

  public NewsArticleResponse toNewsArticleResponse(String title, String date, String content) {
    try {
      return new NewsArticleResponse(title, date, content);
    } catch (Exception e) {
      log.error("NewsArticleResponse 변환 실패: {}", e.getMessage(), e);
      throw new RuntimeException("NewsArticleResponse 변환 실패", e);
    }
  }
}