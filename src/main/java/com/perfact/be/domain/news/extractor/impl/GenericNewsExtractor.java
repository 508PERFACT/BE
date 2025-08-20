package com.perfact.be.domain.news.extractor.impl;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.AbstractNewsExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

// 뉴스 도메인 라우팅 추출기
@Slf4j
@Component
public class GenericNewsExtractor extends AbstractNewsExtractor {

  public GenericNewsExtractor(com.perfact.be.domain.news.service.HtmlParserService htmlParserService,
      com.perfact.be.domain.news.service.DateExtractorService dateExtractorService) {
    super(htmlParserService, dateExtractorService);
  }

  @Override
  public boolean canExtract(String url) {
    // 지원하는 뉴스 사이트들만 처리하고, 나머지는 거부
    return url.contains("news.naver.com") || url.contains("yna.co.kr") || url.contains("newsis.com")
        || url.contains("nocutnews.co.kr"); //|| url.contains("ohmynews.com");
  }

  @Override
  public NewsArticleResponse extract(String url) {
    log.info("지원하는 뉴스 사이트 처리: {}", url);

    try {
      Document doc = getDocument(url);

      String title = extractTitle(doc, getTitleSelectors());
      String content = extractContent(doc, getContentSelectors());
      String date = extractDate(url);

      return new NewsArticleResponse(title, date, content);

    } catch (Exception e) {
      log.error("뉴스 사이트 처리 실패: {}", url, e);
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
