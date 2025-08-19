package com.perfact.be.domain.news.extractor.impl;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.AbstractNewsExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * 연합뉴스 추출기
 */
@Slf4j
@Component
public class YnaNewsExtractor extends AbstractNewsExtractor {

  public YnaNewsExtractor(com.perfact.be.domain.news.service.HtmlParserService htmlParserService,
      com.perfact.be.domain.news.service.DateExtractorService dateExtractorService) {
    super(htmlParserService, dateExtractorService);
  }

  @Override
  public boolean canExtract(String url) {
    return url.contains("yna.co.kr");
  }

  @Override
  public NewsArticleResponse extract(String url) {
    log.info("연합뉴스 추출 시작: {}", url);

    try {
      Document doc = getDocument(url);

      String title = extractTitle(doc, getTitleSelectors());
      String content = extractContent(doc, getContentSelectors());
      String date = extractDate(doc);

      log.info("연합뉴스 추출 완료 - 제목: {}, 날짜: {}, 내용 길이: {}",
          title, date, content.length());

      return new NewsArticleResponse(title, date, content);

    } catch (Exception e) {
      log.error("연합뉴스 추출 실패: {}", url, e);
      throw e;
    }
  }

  /**
   * 연합뉴스에서 날짜를 추출합니다.
   */
  private String extractDate(Document doc) {
    try {
      Element dateElement = doc.selectFirst(".txt-time01");
      if (dateElement != null) {
        String dateText = dateElement.text().trim();
        // "송고2025-08-19 19:29" 형태에서 날짜 부분만 추출
        if (dateText.contains("송고")) {
          String date = dateText.replace("송고", "").trim();
          return date;
        }
        return dateText;
      }
      return "날짜 정보 없음";
    } catch (Exception e) {
      log.warn("연합뉴스 날짜 추출 실패", e);
      return "날짜 정보 없음";
    }
  }

  /**
   * 연합뉴스 본문을 정제합니다.
   */
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

  /**
   * 불필요한 요소들을 제거합니다.
   */
  private void removeUnnecessaryElements(Element contentElement) {
    // 광고 관련 요소 제거
    contentElement.select("aside").remove();

    // 기자 정보 제거
    contentElement.select(".writer-zone01").remove();

    // 이미지 그룹 제거
    contentElement.select(".comp-box.photo-group").remove();

    // 저작권 정보 제거
    contentElement.select(".txt-copyright").remove();

    // 기타 불필요한 요소들 제거
    contentElement.select(".tit-sub").remove();
    contentElement.select(".swiper-area").remove();
  }

  /**
   * 불필요한 텍스트인지 확인합니다.
   */
  private boolean isUnnecessaryText(String text) {
    // 이메일 주소 제거
    if (text.contains("@") && text.contains(".co.kr")) {
      return true;
    }

    // 저작권 관련 텍스트 제거
    if (text.contains("저작권자") || text.contains("무단 전재") || text.contains("AI 학습")) {
      return true;
    }

    // 제보 관련 텍스트 제거
    if (text.contains("제보는 카카오톡")) {
      return true;
    }

    return false;
  }

  @Override
  protected String[] getTitleSelectors() {
    return new String[] {
        "h1.tit01",
        "h1",
        ".title",
        "title"
    };
  }

  @Override
  protected String[] getContentSelectors() {
    return new String[] {
        ".story-news.article",
        ".article",
        ".content",
        "article"
    };
  }
}
