package com.perfact.be.domain.news.extractor.impl;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.AbstractNewsExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

// 뉴시스 추출기
@Slf4j
@Component
public class NewsisNewsExtractor extends AbstractNewsExtractor {

  public NewsisNewsExtractor(com.perfact.be.domain.news.service.HtmlParserService htmlParserService,
      com.perfact.be.domain.news.service.DateExtractorService dateExtractorService) {
    super(htmlParserService, dateExtractorService);
  }

  @Override
  public boolean canExtract(String url) {
    return url.contains("newsis.com");
  }

  @Override
  public NewsArticleResponse extract(String url) {
    log.info("뉴시스 추출 시작: {}", url);

    try {
      Document doc = getDocument(url);

      String title = extractTitle(doc, getTitleSelectors());
      String content = extractContent(doc, getContentSelectors());
      String date = extractDate(doc);

      log.info("뉴시스 추출 완료 - 제목: {}, 날짜: {}, 내용 길이: {}",
          title, date, content.length());

      return new NewsArticleResponse(title, date, content);

    } catch (Exception e) {
      log.error("뉴시스 추출 실패: {}", url, e);
      throw e;
    }
  }

  // 뉴시스에서 날짜 추출
  private String extractDate(Document doc) {
    try {
      Element dateElement = doc.selectFirst(".txt");
      if (dateElement != null) {
        Elements spans = dateElement.select("span");
        for (Element span : spans) {
          String dateText = span.text().trim();
          // "등록 2025.08.19 17:09:47" 형태에서 등록 부분만 추출
          if (dateText.startsWith("등록")) {
            String date = dateText.replace("등록", "").trim();
            return date;
          }
        }
      }
      return "날짜 정보 없음";
    } catch (Exception e) {
      log.warn("뉴시스 날짜 추출 실패", e);
      return "날짜 정보 없음";
    }
  }

  // 뉴시스 본문 정제
  @Override
  protected String processContentElement(Element contentElement) {
    // 불필요한 요소들 제거
    removeUnnecessaryElements(contentElement);

    StringBuilder content = new StringBuilder();

    // p 태그들 처리
    Elements paragraphs = contentElement.select("p");
    for (Element p : paragraphs) {
      String text = p.text().trim();
      if (!text.isEmpty() && !isUnnecessaryText(text)) {
        content.append(text).append("\n\n");
      }
    }

    // li 태그들 처리
    Elements listItems = contentElement.select("li");
    for (Element li : listItems) {
      String text = li.text().trim();
      if (!text.isEmpty() && !isUnnecessaryText(text)) {
        content.append("• ").append(text).append("\n");
      }
    }

    // p, li 태그가 없는 경우 전체 텍스트 추출
    if (content.length() == 0) {
      String fullText = contentElement.text().trim();
      if (!fullText.isEmpty()) {
        String processedText = fullText.replaceAll("\\s+", " ").trim();
        content.append(processedText);
      }
    }

    return content.toString();
  }

  // 불필요한 요소들 제거
  private void removeUnnecessaryElements(Element contentElement) {
    // 광고 관련 요소 제거
    contentElement.select("iframe").remove();
    contentElement.select("#view_ad").remove();

    // 이미지 관련 요소 제거
    contentElement.select(".thumCont").remove();
    contentElement.select(".article_photo").remove();
    contentElement.select(".photojournal").remove();

    // 요약 부분 제거
    contentElement.select(".summury").remove();

    // 스크립트 태그 제거
    contentElement.select("script").remove();

    // 기타 불필요한 요소들 제거
    contentElement.select(".desc").remove();
  }

  // 불필요한 텍스트 확인
  private boolean isUnnecessaryText(String text) {
    // 기자 연락처 제거
    if (text.contains("◎공감언론 뉴시스") || text.contains("@newsis.com")) {
      return true;
    }

    // 이미지 캡션 관련 텍스트 제거
    if (text.contains("[서울=뉴시스]") && text.contains("기자 =")) {
      return true;
    }

    // 날짜 관련 텍스트 제거 (본문에서)
    if (text.contains("등록") || text.contains("수정")) {
      return true;
    }

    return false;
  }

  @Override
  protected String[] getTitleSelectors() {
    return new String[] {
        "h1.tit.title_area",
        "h1.title_area",
        "h1.tit",
        "h1",
        ".title",
        "title"
    };
  }

  @Override
  protected String[] getContentSelectors() {
    return new String[] {
        "article",
        ".content",
        ".article",
        "#textBody"
    };
  }
}
