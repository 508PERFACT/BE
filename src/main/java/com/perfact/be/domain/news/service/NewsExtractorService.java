package com.perfact.be.domain.news.service;

// 뉴스 내용 추출
public interface NewsExtractorService {

  // 뉴스 기사 내용 추출
  String extractNewsArticleContent(String url);

  // 다른 뉴스 사이트 제목 추출
  String extractTitleFromOtherNewsSites(String url);
}