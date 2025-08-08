package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.exception.NewsHandler;
import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
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
      log.info("Original Query: {}", query);

      URI uri = UriComponentsBuilder
          .fromUriString(naverSearchUrl)
          .queryParam("query", query)
          .queryParam("display", 10)
          .queryParam("start", 1)
          .queryParam("sort", "sim")
          .encode(StandardCharsets.UTF_8)
          .build()
          .toUri();

      log.info("Request URI: {}", uri);

      HttpHeaders headers = createNaverHeaders();
      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<String> response = restTemplate.exchange(
          uri, HttpMethod.GET, entity, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody();
      } else {
        throw new NewsHandler(NewsErrorStatus.NEWS_NAVER_API_CALL_FAILED);
      }

    } catch (Exception e) {
      log.error("Error during Naver API call for query: " + query, e);
      throw new NewsHandler(NewsErrorStatus.NEWS_NAVER_API_CALL_FAILED);
    }
  }

  private HttpHeaders createNaverHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Naver-Client-Id", naverClientId);
    headers.set("X-Naver-Client-Secret", naverClientSecret);
    return headers;
  }
}