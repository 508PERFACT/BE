package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.exception.NewsHandler;
import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NaverApiServiceImpl implements NaverApiService {

  private final RestTemplate restTemplate;

  @Value("${api.naver.search-url}")
  private String naverSearchUrl;

  @Value("${api.naver.client-id}")
  private String naverClientId;

  @Value("${api.naver.client-secret}")
  private String naverClientSecret;

  @Override
  public String searchNaverNews(String query) {
    try {
      String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
      String searchUrl = String.format("%s?query=%s&display=10&start=1&sort=sim",
          naverSearchUrl, encodedQuery);

      HttpHeaders headers = createNaverHeaders();
      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<String> response = restTemplate.exchange(
          searchUrl, HttpMethod.GET, entity, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody();
      } else {
        throw new NewsHandler(NewsErrorStatus.NEWS_NAVER_API_CALL_FAILED);
      }

    } catch (Exception e) {
      throw new NewsHandler(NewsErrorStatus.NEWS_NAVER_API_CALL_FAILED);
    }
  }

  // 네이버 API 헤더 생성
  private HttpHeaders createNaverHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Naver-Client-Id", naverClientId);
    headers.set("X-Naver-Client-Secret", naverClientSecret);
    return headers;
  }
}