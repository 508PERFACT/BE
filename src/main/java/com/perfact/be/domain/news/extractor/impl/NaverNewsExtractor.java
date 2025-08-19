package com.perfact.be.domain.news.extractor.impl;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.AbstractNewsExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * 네이버 뉴스 추출기
 */
@Slf4j
@Component
public class NaverNewsExtractor extends AbstractNewsExtractor {

  public NaverNewsExtractor(com.perfact.be.domain.news.service.HtmlParserService htmlParserService,
      com.perfact.be.domain.news.service.DateExtractorService dateExtractorService) {
    super(htmlParserService, dateExtractorService);
  }

  @Override
  public boolean canExtract(String url) {
    return url.contains("news.naver.com");
  }

  @Override
  public NewsArticleResponse extract(String url) {
    log.info("네이버 뉴스 추출 시작: {}", url);

    try {
      Document doc = getDocument(url);

      String title = extractTitle(doc, getTitleSelectors());
      String content = extractContent(doc, getContentSelectors());
      String date = extractDate(url);

      log.info("네이버 뉴스 추출 완료 - 제목: {}, 날짜: {}, 내용 길이: {}",
          title, date, content.length());

      return new NewsArticleResponse(title, date, content);

    } catch (Exception e) {
      log.error("네이버 뉴스 추출 실패: {}", url, e);
      throw e;
    }
  }

  @Override
  protected String[] getTitleSelectors() {
    return new String[] {
        "#title_area span",
        ".title_area .title",
        "h1",
        ".title",
        "[class*=\"title\"]",
        "title"
    };
  }

  @Override
  protected String[] getContentSelectors() {
    return new String[] {
        "#dic_area",
        ".dic_area",
        "article",
        "[id*=\"dic\"]",
        "[class*=\"article\"]"
    };
  }
}
