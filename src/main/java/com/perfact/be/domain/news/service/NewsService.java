package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.dto.NewsArticleResponse;

public interface NewsService {

  // URL에서 HTML 가져오기
  org.jsoup.nodes.Document getHtmlFromUrl(String url);

  // 네이버 뉴스 도메인인지 확인
  boolean isNaverNewsDomain(String url);

  // 네이버 뉴스의 제목과 내용 추출
  NewsArticleResponse extractNaverNewsArticle(String url);

  // 뉴스 기사 내용 추출
  String extractNewsArticleContent(String url);

  // 다른 뉴스 사이트 제목 추출
  String extractTitleFromOtherNewsSites(String url);

  // 네이버 뉴스 검색
  String searchNaverNews(String query);
}