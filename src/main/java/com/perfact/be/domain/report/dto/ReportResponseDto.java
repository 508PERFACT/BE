package com.perfact.be.domain.report.dto;

import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.entity.ReportBadge;
import com.perfact.be.domain.report.entity.TrueScore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "뉴스 분석 리포트 응답 DTO")
public class ReportResponseDto {

  @Schema(description = "리포트 ID", example = "1")
  private Long reportId;

  @Schema(description = "뉴스 제목", example = "뉴스 제목입니다")
  private String title;

  @Schema(description = "뉴스 카테고리", example = "사회")
  private String category;

  @Schema(description = "한 줄 요약", example = "기사의 핵심 내용을 한 문장으로 요약")
  private String oneLineSummary;

  @Schema(description = "뉴스 URL", example = "https://news.naver.com/...")
  private String url;

  @Schema(description = "출판사", example = "네이버뉴스")
  private String publisher;

  @Schema(description = "발행일", example = "2025-08-06")
  private String publicationDate;

  @Schema(description = "요약", example = "뉴스 요약 내용입니다")
  private String summary;

  @Schema(description = "챗봇 컨텍스트", example = "AI 분석 결과입니다")
  private String chatbotContext;

  @Schema(description = "생성일시")
  private LocalDateTime createdAt;

  @Schema(description = "수정일시")
  private LocalDateTime updatedAt;

  @Schema(description = "신뢰도 점수 정보")
  private TrueScoreDto trueScore;

  @Schema(description = "AI 판단 배지 목록")
  private List<ReportBadgeDto> reportBadges;

  public static ReportResponseDto from(Report report, TrueScore trueScore, List<ReportBadge> reportBadges) {
    return ReportResponseDto.builder()
        .reportId(report.getReportId())
        .title(report.getTitle())
        .category(report.getCategory())
        .url(report.getUrl())
        .publisher(report.getPublisher())
        .publicationDate(report.getPublicationDate().toString())
        .summary(report.getSummary())
        .oneLineSummary(report.getOneLineSummary())
        .chatbotContext(report.getChatbotContext())
        .createdAt(report.getCreatedAt())
        .updatedAt(report.getUpdatedAt())
        .trueScore(trueScore != null ? TrueScoreDto.from(trueScore) : null)
        .reportBadges(reportBadges != null ? reportBadges.stream()
            .map(ReportBadgeDto::from)
            .collect(Collectors.toList()) : null)
        .build();
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "신뢰도 점수 DTO")
  public static class TrueScoreDto {

    @Schema(description = "출처 신뢰성 점수", example = "80")
    private Integer sourceReliability;

    @Schema(description = "출처 신뢰성 점수 설명", example = "주요 통신사(연합뉴스) 보도, 기자명 명시")
    private String sourceReliabilityReason;

    @Schema(description = "사실 근거 점수", example = "85")
    private Integer factualBasis;

    @Schema(description = "사실 근거 점수 설명", example = "재난 문자 발송 시각, 지역별 강수량 등 수치 제시, 지자체 인용")
    private String factualBasisReason;

    @Schema(description = "광고/과장 표현 점수", example = "90")
    private Integer adExaggeration;

    @Schema(description = "광고/과장 표현 점수 설명", example = "객관적 사실 전달 중심, 광고/과장 표현 없음")
    private String adExaggerationReason;

    @Schema(description = "편향성 점수", example = "75")
    private Integer bias;

    @Schema(description = "편향성 점수 설명", example = "재난 상황 및 당국 대응을 중립적으로 전달")
    private String biasReason;

    @Schema(description = "기사 형식 점수", example = "88")
    private Integer articleStructure;

    @Schema(description = "기사 형식 점수 설명", example = "제목과 내용 일치, 명확한 문단 구성, 간결한 문체")
    private String articleStructureReason;

    @Schema(description = "전체 신뢰도 점수", example = "84")
    private Integer overallScore;

    public static TrueScoreDto from(TrueScore trueScore) {
      return TrueScoreDto.builder()
          .sourceReliability(trueScore.getSourceReliability())
          .sourceReliabilityReason(trueScore.getSourceReliabilityReason())
          .factualBasis(trueScore.getFactualBasis())
          .factualBasisReason(trueScore.getFactualBasisReason())
          .adExaggeration(trueScore.getAdExaggeration())
          .adExaggerationReason(trueScore.getAdExaggerationReason())
          .bias(trueScore.getBias())
          .biasReason(trueScore.getBiasReason())
          .articleStructure(trueScore.getArticleStructure())
          .articleStructureReason(trueScore.getArticleStructureReason())
          .overallScore(trueScore.getOverallScore())
          .build();
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "리포트 배지 DTO")
  public static class ReportBadgeDto {

    @Schema(description = "배지 ID", example = "1")
    private Long badgeId;

    @Schema(description = "배지 이름", example = "PARTIALLY_TRUSTWORTHY")
    private String badgeName;

    @Schema(description = "배지 설명", example = "부분적인 신뢰 가능")
    private String badgeDescription;

    public static ReportBadgeDto from(ReportBadge reportBadge) {
      return ReportBadgeDto.builder()
          .badgeId(reportBadge.getBadge().getBadgeId())
          .badgeName(reportBadge.getBadge().getBadgeName().name())
          .badgeDescription(reportBadge.getBadge().getBadgeName().getDisplayName())
          .build();
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "레포트 리스트 응답 DTO")
  public static class ReportListDto {

    @Schema(description = "레포트 목록")
    private List<ReportDto> reports;

    @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
    private int currentPage;

    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    @Schema(description = "전체 레포트 수", example = "27")
    private long totalElements;

    @Schema(description = "현재 페이지가 마지막 페이지 여부", example = "false")
    private boolean isLast;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "리포트 응답 DTO")
  public static class ReportDto {

    @Schema(description = "리포트 ID", example = "1")
    private Long reportId;

    @Schema(description = "뉴스 제목", example = "뉴스 제목입니다")
    private String title;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
  }
}