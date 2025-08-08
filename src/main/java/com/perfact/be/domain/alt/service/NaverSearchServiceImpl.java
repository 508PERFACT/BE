package com.perfact.be.domain.alt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.perfact.be.domain.alt.exception.AltHandler;
import com.perfact.be.domain.alt.exception.status.AltErrorStatus;
import com.perfact.be.domain.news.service.NaverApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverSearchServiceImpl implements NaverSearchService {

  private final NaverApiService naverApiService;

  private final ObjectMapper objectMapper;

  @Override
  public String searchNaverNews(String searchQuery) {
    try {
      log.info("네이버 뉴스 검색 요청: {}", searchQuery);

      // 네이버 API 호출
      String searchResult = naverApiService.searchNaverNews(searchQuery);
      log.info("네이버 API 응답: {}", searchResult);

      // JSON 응답 파싱
      JsonNode jsonNode = objectMapper.readTree(searchResult);

      // items 배열에서 첫 번째 결과의 link 추출
      JsonNode items = jsonNode.get("items");
      if (items != null && items.isArray() && items.size() > 0) {
        JsonNode firstItem = items.get(0);
        String link = firstItem.get("link").asText();
        log.info("추출된 뉴스 URL: {}", link);
        return link;
      } else {
        log.error("네이버 검색 결과가 없습니다 - 검색어: {}", searchQuery);
        throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
      }

    } catch (Exception e) {
      log.error("네이버 뉴스 검색 실패: {}", e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
    }
  }
}
