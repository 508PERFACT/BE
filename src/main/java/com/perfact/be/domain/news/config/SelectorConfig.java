package com.perfact.be.domain.news.config;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SelectorConfig {

  // 제목 추출 셀렉터
  public static final List<String> TITLE_SELECTORS = List.of(
      "#title_area span", // 네이버 뉴스의 일반적인 제목 셀렉터
      ".title_area .title", // 기존 셀렉터
      "h1", // 일반적인 제목 태그
      ".title", // 일반적인 제목 클래스
      "[class*=\"title\"]", // 제목 관련 클래스를 포함하는 요소
      "title" // HTML title 태그
  );

  // 다른 뉴스 사이트 제목 셀렉터
  public static final List<String> OTHER_NEWS_TITLE_SELECTORS = List.of(
      "h1",
      ".title",
      ".headline",
      ".article-title",
      "title");

  // 뉴스 내용 추출 셀렉터
  public static final List<String> CONTENT_SELECTORS = List.of(
      "#dic_area", // id 셀렉터 (가장 일반적)
      ".dic_area", // 클래스 셀렉터 (기존)
      "article", // article 태그
      "[id*=\"dic\"]", // dic을 포함하는 id
      "[class*=\"article\"]" // article을 포함하는 클래스
  );

  // 날짜 추출 셀렉터
  public static final List<String> DATE_SELECTORS = List.of(
      ".media_end_head_info_datestamp_time._ARTICLE_DATE_TIME", // 네이버 뉴스 기본
      "[class*='media_end_head_info_datestamp_time']", // 대체 셀렉터
      ".title_area .info .date", // 기존 셀렉터
      "time", // HTML5 time 태그
      ".date", // 일반적인 날짜 클래스
      ".article-date", // 기사 날짜 클래스
      "[class*=\"date\"]", // 날짜 관련 클래스를 포함하는 요소
      ".published-date", // 발행일 클래스
      ".article-time" // 기사 시간 클래스
  );

  /**
   * 제목 셀렉터 배열을 반환합니다.
   */
  public String[] getTitleSelectors() {
    return TITLE_SELECTORS.toArray(new String[0]);
  }

  /**
   * 다른 뉴스 사이트 제목 셀렉터 배열을 반환합니다.
   */
  public String[] getOtherNewsTitleSelectors() {
    return OTHER_NEWS_TITLE_SELECTORS.toArray(new String[0]);
  }

  /**
   * 내용 셀렉터 배열을 반환합니다.
   */
  public String[] getContentSelectors() {
    return CONTENT_SELECTORS.toArray(new String[0]);
  }

  /**
   * 날짜 셀렉터 배열을 반환합니다.
   */
  public String[] getDateSelectors() {
    return DATE_SELECTORS.toArray(new String[0]);
  }
}