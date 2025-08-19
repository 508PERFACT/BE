package com.perfact.be.domain.news.extractor;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.exception.NewsHandler;
import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import com.perfact.be.domain.news.service.DateExtractorService;
import com.perfact.be.domain.news.service.HtmlParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// 뉴스 추출기 추상 클래스
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractNewsExtractor implements NewsExtractorStrategy {

  protected final HtmlParserService htmlParserService;
  protected final DateExtractorService dateExtractorService;

  // HTML 문서에서 제목 추출
  protected String extractTitle(Document doc, String[] titleSelectors) {
    for (String selector : titleSelectors) {
      Element titleElement = doc.selectFirst(selector);
      if (titleElement != null) {
        String title = titleElement.text().trim();
        if (!title.isEmpty()) {
          log.debug("제목 추출 성공: {} -> {}", selector, title);
          return title;
        }
      }
    }
    log.warn("제목을 찾을 수 없습니다. 사용된 셀렉터: {}", String.join(", ", titleSelectors));
    return "제목을 찾을 수 없습니다";
  }

  // HTML 문서에서 내용 추출
  protected String extractContent(Document doc, String[] contentSelectors) {
    for (String selector : contentSelectors) {
      Element contentElement = doc.selectFirst(selector);
      if (contentElement != null) {
        String content = processContentElement(contentElement);
        if (!content.trim().isEmpty()) {
          log.debug("내용 추출 성공: {} -> 길이: {}", selector, content.length());
          return content;
        }
      }
    }
    log.warn("내용을 찾을 수 없습니다. 사용된 셀렉터: {}", String.join(", ", contentSelectors));
    return "내용을 찾을 수 없습니다";
  }

  // 내용 요소 처리
  protected String processContentElement(Element contentElement) {
    StringBuilder content = new StringBuilder();

    // p 태그들 처리
    Elements paragraphs = contentElement.select("p");
    for (Element p : paragraphs) {
      String text = p.text().trim();
      if (!text.isEmpty()) {
        content.append(text).append("\n\n");
      }
    }

    // li 태그들 처리
    Elements listItems = contentElement.select("li");
    for (Element li : listItems) {
      String text = li.text().trim();
      if (!text.isEmpty()) {
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

  // HTML 문서 가져오기
  protected Document getDocument(String url) {
    try {
      return htmlParserService.getHtmlFromUrl(url);
    } catch (Exception e) {
      log.error("HTML 문서 가져오기 실패: {}", url, e);
      throw new NewsHandler(NewsErrorStatus.NEWS_ARTICLE_PARSING_FAILED);
    }
  }

  // 날짜 추출
  protected String extractDate(String url) {
    try {
      return dateExtractorService.extractArticleDate(url);
    } catch (Exception e) {
      log.warn("날짜 추출 실패: {}", url, e);
      return "날짜 정보 없음";
    }
  }

  // 도메인별 제목 셀렉터 반환
  protected abstract String[] getTitleSelectors();

  // 도메인별 내용 셀렉터 반환
  protected abstract String[] getContentSelectors();
}
