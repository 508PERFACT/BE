package com.perfact.be.domain.news.service;

// 네이버 API 호출
public interface NaverApiService {

  // 네이버 뉴스 검색
  String searchNaverNews(String query);
}