package com.perfact.be.domain.news.extractor.impl;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.AbstractNewsExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * 일반 뉴스 사이트 추출기
 * 네이버 뉴스, 연합뉴스, 뉴시스, 노컷뉴스가 아닌 기타 뉴스 사이트를 처리합니다.
 */
@Slf4j
@Component
public class GenericNewsExtractor extends AbstractNewsExtractor {

  public GenericNewsExtractor(com.perfact.be.domain.news.service.HtmlParserService htmlParserService,
      com.perfact.be.domain.news.service.DateExtractorService dateExtractorService) {
    super(htmlParserService, dateExtractorService);
  }

  @Override
  public boolean canExtract(String url) {
    // 네이버 뉴스, 연합뉴스, 뉴시스, 노컷뉴스가 아닌 모든 URL을 처리
    return !url.contains("news.naver.com") && !url.contains("yna.co.kr") && !url.contains("newsis.com")
        && !url.contains("nocutnews.co.kr") && !url.contains("ohmynews.com");
  }

  @Override
  public NewsArticleResponse extract(String url) {
    log.info("일반 뉴스 사이트 추출 시작: {}", url);

    try {
      Document doc = getDocument(url);

      String title = extractTitle(doc, getTitleSelectors());
      String content = extractContent(doc, getContentSelectors());
      String date = extractDate(url);

      log.info("일반 뉴스 사이트 추출 완료 - 제목: {}, 날짜: {}, 내용 길이: {}",
          title, date, content.length());

      return new NewsArticleResponse(title, date, content);

    } catch (Exception e) {
      log.error("일반 뉴스 사이트 추출 실패: {}", url, e);
      throw e;
    }
  }

  @Override
  protected String[] getTitleSelectors() {
    return new String[] {
        "h1",
        ".title",
        ".headline",
        ".article-title",
        "title",
        "[class*=\"title\"]",
        "[class*=\"headline\"]"
    };
  }

  @Override
  protected String[] getContentSelectors() {
    return new String[] {
        "article",
        ".article-content",
        ".content",
        ".post-content",
        ".entry-content",
        "[class*=\"article\"]",
        "[class*=\"content\"]",
        "main",
        ".main-content"
    };
  }
}
