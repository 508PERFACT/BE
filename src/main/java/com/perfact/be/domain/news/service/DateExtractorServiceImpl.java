package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.config.SelectorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DateExtractorServiceImpl implements DateExtractorService {

  private final HtmlParserService htmlParserService;
  private final SelectorConfig selectorConfig;

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

        log.warn("날짜 정보를 찾을 수 없습니다 - URL: {}", url);
        return "날짜 정보 없음";
      }

      return extractDateFromElement(dateElement);

    } catch (Exception e) {
      log.error("날짜 추출 중 예외 발생 - URL: {}, 에러: {}", url, e.getMessage(), e);
      return "날짜 정보 없음";
    }
  }

  // 날짜 파싱
  private String extractDateFromElement(Element dateElement) {
    String dateText = dateElement.text().trim();

    // 정규식을 사용하여 날짜 패턴 찾기
    Pattern pattern = Pattern.compile("(\\d{4})[.-](\\d{1,2})[.-](\\d{1,2})");
    Matcher matcher = pattern.matcher(dateText);

    if (matcher.find()) {
      String year = matcher.group(1);
      String month = matcher.group(2);
      String day = matcher.group(3);

      // 월과 일이 한 자리인 경우 앞에 0 추가
      month = month.length() == 1 ? "0" + month : month;
      day = day.length() == 1 ? "0" + day : day;

      return year + "-" + month + "-" + day;
    }

    return dateText;
  }
}