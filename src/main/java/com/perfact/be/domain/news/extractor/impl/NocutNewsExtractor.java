package com.perfact.be.domain.news.extractor.impl;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.AbstractNewsExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 노컷뉴스 추출기
@Slf4j
@Component
public class NocutNewsExtractor extends AbstractNewsExtractor {

  public NocutNewsExtractor(com.perfact.be.domain.news.service.HtmlParserService htmlParserService,
      com.perfact.be.domain.news.service.DateExtractorService dateExtractorService) {
    super(htmlParserService, dateExtractorService);
  }

  @Override
  public boolean canExtract(String url) {
    return url.contains("nocutnews.co.kr");
  }

  @Override
  public NewsArticleResponse extract(String url) {
    try {
      log.info("노컷뉴스 추출 시작: {}", url);
      Document doc = getDocument(url);

      String title = extractTitle(doc, getTitleSelectors());
      String content = extractContent(doc, getContentSelectors());
      String date = extractDate(doc);

      log.info("노컷뉴스 추출 완료 - 제목: {}, 날짜: {}, 내용 길이: {}", title, date, content.length());

      return new NewsArticleResponse(title, date, content);
    } catch (Exception e) {
      log.error("노컷뉴스 추출 실패: {}", url, e);
      throw new RuntimeException("노컷뉴스 기사 추출에 실패했습니다: " + url, e);
    }
  }

  // 노컷뉴스 특화 날짜 추출
  private String extractDate(Document doc) {
    try {
      Elements dateElements = doc.select("ul.bl_b li");

      // 두 번째 li가 있는지 확인
      if (dateElements.size() >= 2) {
        Element secondLi = dateElements.get(1); // 두 번째 li (인덱스 1)
        String text = secondLi.text().trim();

        // 날짜 패턴 확인
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}");
        Matcher matcher = datePattern.matcher(text);

        if (matcher.find()) {
          return matcher.group();
        } else {
          log.warn("노컷뉴스 두 번째 li에서 날짜 패턴을 찾을 수 없습니다: {}", text);
        }
      } else {
        log.warn("노컷뉴스 ul.bl_b에 li가 2개 미만입니다. 실제 개수: {}", dateElements.size());
      }

      // 기존 방식으로도 시도 (fallback)
      Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}");
      for (Element element : dateElements) {
        String text = element.text().trim();
        Matcher matcher = datePattern.matcher(text);
        if (matcher.find()) {
          log.info("fallback 방식으로 날짜 추출 성공: {}", matcher.group());
          return matcher.group();
        }
      }

      log.warn("노컷뉴스 날짜를 찾을 수 없습니다");
      return "날짜 정보 없음";
    } catch (Exception e) {
      log.error("노컷뉴스 날짜 추출 실패", e);
      return "날짜 정보 없음";
    }
  }

  @Override
  protected String processContentElement(Element contentElement) {
    // 불필요한 요소들 제거
    removeUnnecessaryElements(contentElement);

    // 텍스트 추출 및 정제
    String content = contentElement.text();

    // 불필요한 텍스트 필터링
    String[] lines = content.split("\n");
    StringBuilder cleanedContent = new StringBuilder();

    for (String line : lines) {
      line = line.trim();
      if (!line.isEmpty() && !isUnnecessaryText(line)) {
        cleanedContent.append(line).append("\n");
      }
    }

    return cleanedContent.toString().trim();
  }

  // 불필요한 HTML 요소들 제거
  private void removeUnnecessaryElements(Element contentElement) {
    // 광고 관련 요소 제거
    contentElement.select("iframe").remove();
    contentElement.select("div[style*='text-align: right'][style*='float: right']").remove();

    // 관련기사 제거
    contentElement.select(".news-related_n").remove();

    // 이미지 관련 요소 제거 (캡션은 유지)
    contentElement.select(".fr-img-space-wrap").remove();

    // 기타 불필요한 요소들 제거
    contentElement.select("script").remove();
    contentElement.select("style").remove();
  }

  // 불필요한 텍스트 확인
  private boolean isUnnecessaryText(String text) {
    if (text == null || text.trim().isEmpty()) {
      return true;
    }

    // 광고 관련 텍스트 제거
    if (text.contains("광고") || text.contains("sponsored")) {
      return true;
    }

    // 관련기사 관련 텍스트 제거
    if (text.contains("관련 기사") || text.contains("추천 기사")) {
      return true;
    }

    // 날짜 패턴이지만 기사 내용이 아닌 경우 제거
    if (text.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}")) {
      return true;
    }

    return false;
  }

  @Override
  protected String[] getTitleSelectors() {
    return new String[] { "div.h_info h2", "h2", ".title", "title" };
  }

  @Override
  protected String[] getContentSelectors() {
    return new String[] { "div#pnlContent", "#pnlContent", ".content", "article" };
  }
}
