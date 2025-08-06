package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.config.SelectorConfig;
import com.perfact.be.domain.news.exception.NewsHandler;
import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsExtractorServiceImpl implements NewsExtractorService {

  private final HtmlParserService htmlParserService;
  private final SelectorConfig selectorConfig;

  // 뉴스 기사 내용 추출
  @Override
  public String extractNewsArticleContent(String url) {
    try {
      Document doc = htmlParserService.getHtmlFromUrl(url);
      StringBuilder content = new StringBuilder();

      Element titleArea = doc.selectFirst(".title_area .title");
      if (titleArea != null) {
        content.append("제목: ").append(titleArea.text().trim()).append("\n\n");
      }

      String extractedContent = extractContentFromDocument(doc);
      if (!extractedContent.trim().isEmpty()) {
        content.append(extractedContent);
      }

      return content.toString();

    } catch (Exception e) {
      throw new NewsHandler(NewsErrorStatus.NEWS_CONTENT_NOT_FOUND);
    }
  }

  // 뉴스 기사 내용 추출
  private String extractContentFromDocument(Document doc) {
    String[] contentSelectors = selectorConfig.getContentSelectors();

    for (String selector : contentSelectors) {
      Element dicArea = doc.selectFirst(selector);
      if (dicArea != null) {
        String extractedContent = processDicArea(dicArea);
        if (!extractedContent.trim().isEmpty()) {
          return extractedContent;
        }
      }
    }

    return "";
  }

  // 뉴스 기사 내용 추출
  private String processDicArea(Element dicArea) {
    StringBuilder content = new StringBuilder();

    // 먼저 p 태그들을 처리
    Elements paragraphs = dicArea.select("p");
    for (Element p : paragraphs) {
      String text = p.text().trim();
      if (!text.isEmpty()) {
        content.append(text).append("\n\n");
      }
    }

    // li 태그들을 처리
    Elements listItems = dicArea.select("li");
    for (Element li : listItems) {
      String text = li.text().trim();
      if (!text.isEmpty()) {
        content.append("• ").append(text).append("\n");
      }
    }

    // p, li 태그가 없는 경우 전체 텍스트를 추출
    if (content.length() == 0) {
      String fullText = dicArea.text().trim();
      if (!fullText.isEmpty()) {
        // <br> 태그를 줄바꿈으로 변환
        String processedText = fullText.replaceAll("\\s+", " ").trim();
        content.append(processedText);
      }
    }

    return content.toString();
  }

  // 다른 뉴스 사이트 제목 추출
  @Override
  public String extractTitleFromOtherNewsSites(String url) {
    try {
      String[] titleSelectors = selectorConfig.getOtherNewsTitleSelectors();

      String title = htmlParserService.extractTextWithMultipleSelectors(url, titleSelectors);
      return title != null ? title : "제목을 찾을 수 없습니다";

    } catch (Exception e) {
      return "제목을 찾을 수 없습니다";
    }
  }
}