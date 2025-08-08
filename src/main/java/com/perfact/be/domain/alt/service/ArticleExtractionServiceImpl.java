package com.perfact.be.domain.alt.service;

import com.perfact.be.domain.alt.dto.ArticleExtractionResult;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleExtractionServiceImpl implements ArticleExtractionService {

  private final NewsService newsService;

  @Override
  public String extractArticleContent(String url) {
    try {
      if (newsService.isNaverNewsDomain(url)) {
        NewsArticleResponse newsData = newsService.extractNaverNewsArticle(url);
        return newsData.getContent();
      } else {
        return newsService.extractNewsArticleContent(url);
      }
    } catch (Exception e) {
      log.error("기사 본문 추출 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public ArticleExtractionResult extractArticleWithMetadata(String url) {
    try {
      if (newsService.isNaverNewsDomain(url)) {
        NewsArticleResponse newsData = newsService.extractNaverNewsArticle(url);
        return ArticleExtractionResult.builder()
            .title(newsData.getTitle())
            .publicationDate(newsData.getDate())
            .content(newsData.getContent())
            .build();
      } else {
        String title = newsService.extractTitleFromOtherNewsSites(url);
        String content = newsService.extractNewsArticleContent(url);
        return ArticleExtractionResult.builder()
            .title(title)
            .publicationDate("날짜 정보 없음")
            .content(content)
            .build();
      }
    } catch (Exception e) {
      log.error("기사 메타데이터 추출 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}