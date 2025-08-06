package com.perfact.be.domain.news.service;

import com.perfact.be.domain.news.config.SelectorConfig;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.exception.NewsExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

  private final HtmlParserService htmlParserService;
  private final NaverApiService naverApiService;
  private final NewsExtractorService newsExtractorService;
  private final DateExtractorService dateExtractorService;
  private final NewsExceptionHandler exceptionHandler;
  private final SelectorConfig selectorConfig;

  @Override
  public org.jsoup.nodes.Document getHtmlFromUrl(String url) {
    return htmlParserService.getHtmlFromUrl(url);
  }

  private String extractTitleAreaText(String url) {
    return exceptionHandler.safeExtractText(url, "extract title", () -> {
      String[] titleSelectors = selectorConfig.getTitleSelectors();

      for (String selector : titleSelectors) {
        String title = htmlParserService.extractTextFromElement(url, selector);
        if (title != null && !title.trim().isEmpty()) {
          return title;
        }
      }

      return null;
    });
  }

  @Override
  public String extractNewsArticleContent(String url) {
    return newsExtractorService.extractNewsArticleContent(url);
  }

  @Override
  public boolean isNaverNewsDomain(String url) {
    return url.contains("news.naver.com");
  }

  @Override
  public NewsArticleResponse extractNaverNewsArticle(String url) {
    try {
      String title = extractTitleAreaText(url);
      if (title == null) {
        exceptionHandler.handleTitleExtractionFailure(url, "extract Naver news article",
            new Exception("Title extraction failed"));
      }

      String date = dateExtractorService.extractArticleDate(url);
      String content = extractNewsArticleContent(url);

      return new NewsArticleResponse(title, date, content);

    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public String extractTitleFromOtherNewsSites(String url) {
    return newsExtractorService.extractTitleFromOtherNewsSites(url);
  }

  @Override
  public String searchNaverNews(String query) {
    return naverApiService.searchNaverNews(query);
  }
}
