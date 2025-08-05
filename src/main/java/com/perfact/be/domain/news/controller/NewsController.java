package com.perfact.be.domain.news.controller;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.service.NewsService;
import com.perfact.be.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

  private final NewsService newsService;

  @GetMapping("/article-content")
  public ApiResponse<NewsArticleResponse> getNewsArticleContent(@RequestParam String url) {
    NewsArticleResponse response = newsService.extractNaverNewsArticle(url);
    return ApiResponse.onSuccess(response);
  }

  @GetMapping("/search")
  public ApiResponse<String> searchNaverNews(@RequestParam String query) {
    String searchResult = newsService.searchNaverNews(query);
    return ApiResponse.onSuccess(searchResult);
  }
}