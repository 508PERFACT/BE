package com.perfact.be.domain.report.converter;

import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.report.dto.ClovaResponseDTO;
import com.perfact.be.domain.report.dto.ReportResponseDto;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.exception.ReportHandler;
import com.perfact.be.domain.report.exception.status.ReportErrorStatus;
import com.perfact.be.domain.report.util.ClovaResponseParser;
import com.perfact.be.domain.report.util.DateParser;
import com.perfact.be.domain.report.util.UrlParser;
import com.perfact.be.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportConverter {

  private final ClovaResponseParser clovaResponseParser;
  private final UrlParser urlParser;
  private final DateParser dateParser;

  // Clova API 응답과 뉴스 데이터를 Report 엔티티로 변환
  public Report toReport(ClovaResponseDTO clovaResponse, NewsArticleResponse newsData, User user, String url) {
    try {
      String content = clovaResponse.getResult().getMessage().getContent();

      String category = clovaResponseParser.extractCategory(content);
      String summary = clovaResponseParser.extractSummary(content);
      String publisher = urlParser.extractPublisher(url);

      return Report.builder()
          .user(user)
          .title(newsData.getTitle())
          .category(category)
          .url(url)
          .publisher(publisher)
          .publicationDate(dateParser.parsePublicationDate(newsData.getDate()))
          .summary(summary)
          .build();
    } catch (Exception e) {
      log.error("리포트 변환 실패: {}", e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.REPORT_CONVERSION_FAILED);
    }
  }

  public ReportResponseDto.ReportDto toDto(Report report) {
    return ReportResponseDto.ReportDto.builder()
        .reportId(report.getReportId())
        .title(report.getTitle())
        .createdAt(report.getCreatedAt())
        .build();
  }

  public List<ReportResponseDto.ReportDto> toDtoList(List<Report> reports) {
    return reports.stream()
        .map(this::toDto)
        .toList();
  }

  public ReportResponseDto.ReportListDto toListDto(Page<Report> reportsPage) {
    return ReportResponseDto.ReportListDto.builder()
        .reports(toDtoList(reportsPage.getContent()))
        .currentPage(reportsPage.getNumber() + 1)
        .totalPages(reportsPage.getTotalPages())
        .totalElements(reportsPage.getTotalElements())
        .isLast(reportsPage.isLast())
        .build();
  }

}