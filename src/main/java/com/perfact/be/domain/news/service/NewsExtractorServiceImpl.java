package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.config.SelectorConfig;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.exception.NewsHandler;
import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import com.perfact.be.domain.news.extractor.factory.NewsExtractorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsExtractorServiceImpl implements NewsExtractorService {

  private final NewsExtractorFactory newsExtractorFactory;
  private final HtmlParserService htmlParserService;
  private final SelectorConfig selectorConfig;

  // 뉴스 기사 내용 추출 (기존 메서드 유지 - 호환성)
  @Override
  public String extractNewsArticleContent(String url) {
    try {
      log.info("뉴스 기사 내용 추출 시작: {}", url);

      // 새로운 팩토리 패턴 사용
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);

      log.info("뉴스 기사 내용 추출 완료 - 제목: {}, 내용 길이: {}",
          newsData.getTitle(), newsData.getContent().length());

      return newsData.getContent();

    } catch (Exception e) {
      log.error("뉴스 기사 내용 추출 실패: {}", url, e);
      throw new NewsHandler(NewsErrorStatus.NEWS_CONTENT_NOT_FOUND);
    }
  }

  // 다른 뉴스 사이트 제목 추출 (기존 메서드 유지 - 호환성)
  @Override
  public String extractTitleFromOtherNewsSites(String url) {
    try {
      log.info("다른 뉴스 사이트 제목 추출 시작: {}", url);

      // 새로운 팩토리 패턴 사용
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);

      log.info("다른 뉴스 사이트 제목 추출 완료: {}", newsData.getTitle());

      return newsData.getTitle();

    } catch (Exception e) {
      log.error("다른 뉴스 사이트 제목 추출 실패: {}", url, e);
      return "제목을 찾을 수 없습니다";
    }
  }
}