package com.perfact.be.domain.news.extractor.factory;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.NewsExtractorStrategy;
import com.perfact.be.domain.news.exception.NewsHandler;
import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 뉴스 추출기 팩토리
 * URL에 따라 적절한 추출기를 선택합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsExtractorFactory {

  private final List<NewsExtractorStrategy> extractors;

  /**
   * URL에 맞는 추출기를 찾아 뉴스를 추출합니다.
   *
   * @param url 뉴스 URL
   * @return 추출된 뉴스 데이터
   */
  public NewsArticleResponse extractNews(String url) {
    log.info("뉴스 추출기 선택 시작: {}", url);

    NewsExtractorStrategy extractor = getExtractor(url);
    log.info("선택된 추출기: {}", extractor.getClass().getSimpleName());

    return extractor.extract(url);
  }

  /**
   * URL에 맞는 추출기를 찾습니다.
   *
   * @param url 뉴스 URL
   * @return 적절한 추출기
   */
  public NewsExtractorStrategy getExtractor(String url) {
    return extractors.stream()
        .filter(extractor -> extractor.canExtract(url))
        .findFirst()
        .orElseThrow(() -> {
          log.error("지원하지 않는 뉴스 사이트입니다: {}", url);
          return new NewsHandler(NewsErrorStatus.UNSUPPORTED_NEWS_SITE);
        });
  }

  /**
   * 사용 가능한 모든 추출기를 반환합니다.
   *
   * @return 추출기 목록
   */
  public List<NewsExtractorStrategy> getAllExtractors() {
    return extractors;
  }
}
