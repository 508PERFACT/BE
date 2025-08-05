package com.perfact.be.domain.news.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

// HTML 파싱
public interface HtmlParserService {

  // URL에서 HTML 가져오기
  Document getHtmlFromUrl(String url);

  // CSS 셀렉터로 요소 특정
  Element extractElementBySelector(String url, String cssSelector);

  // CSS 셀렉터로 텍스트 추출
  String extractTextFromElement(String url, String cssSelector);

  // 여러 CSS 셀렉터를 시도하여 텍스트 추출
  String extractTextWithMultipleSelectors(String url, String[] selectors);
}