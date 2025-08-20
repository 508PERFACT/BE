package com.perfact.be.domain.news.extractor;

import com.perfact.be.domain.news.dto.NewsArticleResponse;

// 뉴스 추출 전략 인터페이스 - 각 도메인별 뉴스 추출 로직 정의
public interface NewsExtractorStrategy {

  // 해당 URL이 이 추출기로 처리 가능한지 확인
  boolean canExtract(String url);

  // 뉴스 기사를 추출
  NewsArticleResponse extract(String url);
}
