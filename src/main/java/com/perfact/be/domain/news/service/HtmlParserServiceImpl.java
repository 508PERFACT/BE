package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.exception.status.NewsErrorStatus;
import com.perfact.be.global.exception.GeneralException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HtmlParserServiceImpl implements HtmlParserService {

  private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

  @Override
  public Document getHtmlFromUrl(String url) {
    try {
      Document doc = Jsoup.connect(url)
          .userAgent(USER_AGENT)
          .timeout(10000)
          .get();

      return doc;

    } catch (IOException e) {
      throw new GeneralException(NewsErrorStatus.NEWS_ARTICLE_PARSING_FAILED);
    }
  }

  @Override
  public Element extractElementBySelector(String url, String cssSelector) {
    try {
      Document doc = getHtmlFromUrl(url);
      Element element = doc.selectFirst(cssSelector);

      if (element == null) {
        return null;
      }

      return element;

    } catch (Exception e) {
      throw new GeneralException(NewsErrorStatus.NEWS_ARTICLE_PARSING_FAILED);
    }
  }

  @Override
  public String extractTextFromElement(String url, String cssSelector) {
    Element element = extractElementBySelector(url, cssSelector);
    return element != null ? element.text().trim() : null;
  }

  @Override
  public String extractTextWithMultipleSelectors(String url, String[] selectors) {
    for (String selector : selectors) {
      String text = extractTextFromElement(url, selector);
      if (text != null && !text.trim().isEmpty()) {
        return text;
      }
    }
    return null;
  }
}