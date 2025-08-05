package com.perfact.be.domain.report.converter;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.report.dto.ClovaResponseDTO;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class ReportConverter {

  // Clova API 응답과 뉴스 데이터를 Report 엔티티로 변환
  public Report toReport(ClovaResponseDTO clovaResponse, NewsArticleResponse newsData, User user, String url) {
    try {
      String category = extractCategoryFromAnalysis(clovaResponse);
      String summary = extractSummaryFromAnalysis(clovaResponse);
      String publisher = extractPublisherFromUrl(url);

      return Report.builder()
          .user(user)
          .title(newsData.getTitle())
          .category(category)
          .url(url)
          .publisher(publisher)
          .publicationDate(parsePublicationDate(newsData.getDate()))
          .summary(summary)
          .build();
    } catch (Exception e) {
      throw new RuntimeException("리포트 변환 실패", e);
    }
  }

  // 분석 결과에서 카테고리 추출
  private String extractCategoryFromAnalysis(ClovaResponseDTO clovaResponse) {
    try {
      String content = clovaResponse.getResult().getMessage().getContent();
      if (content.contains("\"field\"")) {
        int start = content.indexOf("\"field\"") + 8;
        int end = content.indexOf("\"", start);
        if (start > 7 && end > start) {
          return content.substring(start, end);
        }
      }
      return "기타";
    } catch (Exception e) {
      return "기타";
    }
  }

  // 분석 결과에서 요약 추출
  private String extractSummaryFromAnalysis(ClovaResponseDTO clovaResponse) {
    try {
      String content = clovaResponse.getResult().getMessage().getContent();
      if (content.contains("\"summary\"")) {
        int start = content.indexOf("\"summary\"") + 10;
        int end = content.indexOf("]", start);
        if (start > 9 && end > start) {
          return content.substring(start, end).replaceAll("\"", "").replaceAll(",", "\n");
        }
      }
      return "";
    } catch (Exception e) {
      return "";
    }
  }

  // URL에서 출판사 추출
  private String extractPublisherFromUrl(String url) {
    try {
      String domain = url.replaceAll("https?://", "").replaceAll("www\\.", "");
      return domain.split("/")[0];
    } catch (Exception e) {
      return "unknown";
    }
  }

  // 날짜 문자열을 LocalDate로 변환
  private LocalDate parsePublicationDate(String dateStr) {
    try {
      if (dateStr == null || dateStr.equals("날짜 정보 없음")) {
        return LocalDate.now();
      }
      return LocalDate.parse(dateStr.substring(0, 10));
    } catch (Exception e) {
      return LocalDate.now();
    }
  }
}