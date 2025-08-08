package com.perfact.be.domain.alt.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class HttpUtil {

  // Clova API용 HTTP 헤더 생성
  public HttpHeaders createClovaHeaders(String apiKey) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);
    return headers;
  }

  // 네이버 API용 HTTP 헤더 생성
  public HttpHeaders createNaverHeaders(String clientId, String clientSecret) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Naver-Client-Id", clientId);
    headers.set("X-Naver-Client-Secret", clientSecret);
    headers.set("User-Agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
    headers.set("Accept", "application/json");
    headers.set("Accept-Encoding", "identity");
    headers.set("Connection", "keep-alive");
    return headers;
  }

  // 검색어 URL 인코딩
  public String encodeSearchQuery(String searchQuery) {
    try {
      return URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("검색어 인코딩 실패: {}", e.getMessage(), e);
      throw new RuntimeException("검색어 인코딩 실패", e);
    }
  }

  // 네이버 검색 URL 생성
  public String createNaverSearchUrl(String baseUrl, String searchQuery) {
    String encodedQuery = encodeSearchQuery(searchQuery);
    return String.format("%s?query=%s&display=1&start=1&sort=sim", baseUrl, encodedQuery);
  }
}
