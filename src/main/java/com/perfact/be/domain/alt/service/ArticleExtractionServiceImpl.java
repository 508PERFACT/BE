package com.perfact.be.domain.alt.service;

import com.perfact.be.domain.alt.dto.ArticleExtractionResult;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.factory.NewsExtractorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleExtractionServiceImpl implements ArticleExtractionService {

  private final NewsExtractorFactory newsExtractorFactory;

  @Override
  public String extractArticleContent(String url) {
    try {
      // 모든 뉴스 사이트에 대해 동일한 방식으로 처리
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);
      return newsData.getContent();
    } catch (Exception e) {
      log.error("기사 본문 추출 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public ArticleExtractionResult extractArticleWithMetadata(String url) {
    try {
      // 모든 뉴스 사이트에 대해 동일한 방식으로 처리
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);
      return ArticleExtractionResult.builder()
          .title(newsData.getTitle())
          .publicationDate(newsData.getDate())
          .content(newsData.getContent())
          .build();
    } catch (Exception e) {
      log.error("기사 메타데이터 추출 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}