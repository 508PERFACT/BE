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

// 오마이뉴스 추출기
@Slf4j
@Component
public class OhMyNewsExtractor extends AbstractNewsExtractor {

  public OhMyNewsExtractor(com.perfact.be.domain.news.service.HtmlParserService htmlParserService,
      com.perfact.be.domain.news.service.DateExtractorService dateExtractorService) {
    super(htmlParserService, dateExtractorService);
  }

  @Override
  public boolean canExtract(String url) {
    return url.contains("ohmynews.com");
  }

  @Override
  public NewsArticleResponse extract(String url) {
    try {
      log.info("오마이뉴스 추출 시작: {}", url);
      Document doc = getDocument(url);

      String title = extractTitle(doc, getTitleSelectors());
      String content = extractContent(doc, getContentSelectors());
      String date = extractDate(doc);

      log.info("오마이뉴스 추출 완료 - 제목: {}, 날짜: {}, 내용 길이: {}", title, date, content.length());

      return new NewsArticleResponse(title, date, content);
    } catch (Exception e) {
      log.error("오마이뉴스 추출 실패: {}", url, e);
      throw new RuntimeException("오마이뉴스 기사 추출에 실패했습니다: " + url, e);
    }
  }

  @Override
  protected String[] getTitleSelectors() {
    return new String[] {
        "h2.article_tit a",
        "h2.article_tit",
        ".article_tit a",
        ".article_tit"
    };
  }

  @Override
  protected String[] getContentSelectors() {
    return new String[] {
        "div.at_contents[itemprop='articleBody']",
        "div.at_contents",
        ".at_contents"
    };
  }

  // 오마이뉴스 특화 날짜 추출
  private String extractDate(Document doc) {
    try {
      // 오마이뉴스 날짜 선택자들 (우선순위 순)
      String[] dateSelectors = {
          "div.atc-sponsor span.date", // 기존 셀렉터
          "span.date", // 직접 span.date
          ".date", // 클래스로만
          "[class*='date']" // 클래스에 date 포함
      };

      for (String selector : dateSelectors) {
        Elements dateElements = doc.select(selector);

        if (!dateElements.isEmpty()) {
          Element firstDateElement = dateElements.first();
          String dateText = firstDateElement.text().trim();

          log.debug("오마이뉴스 원본 날짜 텍스트: {}", dateText);

          // "25.08.19 15:25" 또는 "25.08.19 19:00" 형식을 "2025-08-19 15:25" 형식으로 변환
          String convertedDate = convertOhMyNewsDate(dateText);

          if (convertedDate != null) {
            log.info("오마이뉴스 날짜 변환 성공: {} → {}", dateText, convertedDate);
            return convertedDate;
          }
        }
      }

      log.warn("오마이뉴스 날짜를 찾을 수 없습니다");
      return "날짜 정보 없음";
    } catch (Exception e) {
      log.error("오마이뉴스 날짜 추출 실패", e);
      return "날짜 정보 없음";
    }
  }

  // 오마이뉴스 날짜 형식 변환
  private String convertOhMyNewsDate(String dateText) {
    try {
      // "25.08.19 15:25" 또는 "25.08.19 19:00" 형식 매칭 (시간이 1자리 또는 2자리)
      Pattern pattern = Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{2})\\s+(\\d{1,2}):(\\d{2})");
      Matcher matcher = pattern.matcher(dateText);

      if (matcher.find()) {
        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);
        int hour = Integer.parseInt(matcher.group(4));
        String minute = matcher.group(5);

        // 20xx년으로 변환 (25 → 2025)
        String fullYear = "20" + year;

        // 시간을 2자리로 포맷팅
        String formattedHour = String.format("%02d", hour);

        return String.format("%s-%s-%s %s:%s", fullYear, month, day, formattedHour, minute);
      }

      return null;
    } catch (Exception e) {
      log.error("오마이뉴스 날짜 변환 실패: {}", dateText, e);
      return null;
    }
  }

  @Override
  protected String processContentElement(Element contentElement) {
    // 오마이뉴스 특화 요소 제거
    removeOhMyNewsSpecificElements(contentElement);

    return contentElement.text().trim();
  }

  // 오마이뉴스 특화 불필요한 요소들 제거
  private void removeOhMyNewsSpecificElements(Element contentElement) {
    // 광고 관련 요소들 제거
    contentElement.select("div[id*='ad'], div[id*='Ad'], .ad, .ads, .advertisement").remove();
    contentElement.select("script, style, iframe").remove();

    // 오마이뉴스 특화 요소들 제거
    contentElement.select("div.dvCenterAd, .V0999, .text").remove();
    contentElement.select("button.zoom-btn, button.rhksfus").remove();
    contentElement.select("figure.omn-photo").remove();

    // 이미지 관련 요소들 제거
    contentElement.select("figure, .pho-center, .pho-caption").remove();
    contentElement.select("img[src*='ohmynews.com']").remove();

    // 기타 불필요한 요소들
    contentElement.select("div[id*='google'], div[id*='Google']").remove();
    contentElement.select("div[class*='ad'], div[class*='Ad']").remove();

    // HTML 주석 제거
    contentElement.select("*").forEach(element -> {
      if (element.nodeName().equals("#comment")) {
        element.remove();
      }
    });
  }
}
