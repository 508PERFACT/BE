package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.config.SelectorConfig;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.exception.NewsExceptionHandler;
import com.perfact.be.domain.news.extractor.factory.NewsExtractorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

  private final NewsExtractorFactory newsExtractorFactory;
  private final HtmlParserService htmlParserService;
  private final NaverApiService naverApiService;
  private final NewsExtractorService newsExtractorService;
  private final DateExtractorService dateExtractorService;
  private final NewsExceptionHandler exceptionHandler;
  private final SelectorConfig selectorConfig;

  @Override
  public org.jsoup.nodes.Document getHtmlFromUrl(String url) {
    return htmlParserService.getHtmlFromUrl(url);
  }

  @Override
  public String extractNewsArticleContent(String url) {
    return newsExtractorService.extractNewsArticleContent(url);
  }

  @Override
  public boolean isNaverNewsDomain(String url) {
    return url.contains("news.naver.com");
  }

  @Override
  public NewsArticleResponse extractNaverNewsArticle(String url) {
    try {
      log.info("네이버 뉴스 기사 추출 시작: {}", url);

      // 새로운 팩토리 패턴 사용
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);

      log.info("네이버 뉴스 기사 추출 완료 - 제목: {}, 날짜: {}, 내용 길이: {}",
          newsData.getTitle(), newsData.getDate(), newsData.getContent().length());

      return newsData;

    } catch (Exception e) {
      log.error("네이버 뉴스 기사 추출 실패: {}", url, e);
      return null;
    }
  }

  @Override
  public String extractTitleFromOtherNewsSites(String url) {
    return newsExtractorService.extractTitleFromOtherNewsSites(url);
  }

  @Override
  public String searchNaverNews(String query) {
    return naverApiService.searchNaverNews(query);
  }
}
