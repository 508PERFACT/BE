package com.perfact.be.domain.news.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DateExtractorServiceImpl implements DateExtractorService {

  private final HtmlParserService htmlParserService;
  private final com.perfact.be.domain.news.config.SelectorConfig selectorConfig;

  @Override
  public String extractArticleDate(String url) {
    try {
      Document doc = htmlParserService.getHtmlFromUrl(url);

      // media_end_head_info_datestamp_time _ARTICLE_DATE_TIME 클래스를 가진 요소 찾기
      Element dateElement = doc.selectFirst(".media_end_head_info_datestamp_time._ARTICLE_DATE_TIME");

      if (dateElement == null) {
        // 대체 선택자 시도
        dateElement = doc.selectFirst("[class*='media_end_head_info_datestamp_time']");
      }

      if (dateElement == null) {
        // 여러 CSS 셀렉터를 시도하여 날짜 추출
        String[] dateSelectors = selectorConfig.getDateSelectors();

        for (String selector : dateSelectors) {
          dateElement = doc.selectFirst(selector);
          if (dateElement != null) {
            String date = dateElement.text().trim();
            if (!date.isEmpty()) {
              return date;
            }
          }
        }

        return "날짜 정보 없음";
      }

      return extractDateFromElement(dateElement);

    } catch (Exception e) {
      return "날짜 정보 없음";
    }
  }

  // 날짜 파싱
  private String extractDateFromElement(Element dateElement) {
    String dataDateTime = dateElement.attr("data-date-time");
    if (!dataDateTime.isEmpty()) {
      String[] parts = dataDateTime.split(" ");
      if (parts.length > 0) {
        String datePart = parts[0];
        return datePart.replace("-", ".");
      }
    }

    String text = dateElement.text();
    if (!text.isEmpty()) {
      Pattern pattern = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})");
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        return matcher.group(1);
      }
    }

    return "날짜 정보 없음";
  }
}