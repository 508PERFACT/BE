package com.perfact.be.domain.news.controller;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.service.NewsService;
import com.perfact.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "News", description = "뉴스 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

  private final NewsService newsService;

  @Operation(summary = "뉴스 기사 내용 추출", description = "네이버 뉴스 URL을 입력받아 기사의 제목, 날짜, 내용을 추출합니다.")
  @GetMapping("/article-content")
  public ApiResponse<NewsArticleResponse> getNewsArticleContent(
      @Parameter(description = "네이버 뉴스 URL", required = true, example = "https://news.naver.com/main/read.naver?mode=LSD&mid=shm&sid1=100&oid=001&aid=0012345678") @RequestParam String url) {
    NewsArticleResponse response = newsService.extractNaverNewsArticle(url);
    return ApiResponse.onSuccess(response);
  }

  @Operation(summary = "네이버 뉴스 검색", description = "검색어를 입력받아 네이버 뉴스 검색 결과를 반환합니다.")
  @GetMapping("/search")
  public ApiResponse<String> searchNaverNews(
      @Parameter(description = "검색할 키워드", required = true, example = "AI 기술") @RequestParam String query) {
    String searchResult = newsService.searchNaverNews(query);
    return ApiResponse.onSuccess(searchResult);
  }
}