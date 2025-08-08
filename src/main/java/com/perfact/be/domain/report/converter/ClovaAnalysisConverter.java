package com.perfact.be.domain.report.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.report.entity.Badge;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.entity.ReportBadge;
import com.perfact.be.domain.report.entity.TrueScore;
import com.perfact.be.domain.report.exception.ReportHandler;
import com.perfact.be.domain.report.exception.status.ReportErrorStatus;
import com.perfact.be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaAnalysisConverter {

  private final ObjectMapper objectMapper;

  public Report convertToReport(String analysisResult, NewsArticleResponse newsData, User user, String url) {
    try {
      JsonNode analysis = objectMapper.readTree(analysisResult);

      // 원본 분석 결과 로깅
      log.debug("=== 클로바 분석 결과 원본 ===");
      log.debug("analysisResult: {}", analysisResult);
      log.debug("analysis JSON: {}", analysis.toString());

      // 필수 필드들이 존재하는지 확인하고 안전하게 추출
      String category = getStringValue(analysis, "field", "분류 없음");
      String oneLineSummary = getStringValue(analysis, "one_line_summary", "한 줄 요약 없음");
      String source = getStringValue(analysis, "source", "출처 없음");
      String chatbotContext = getStringValue(analysis, "chatbot_context", "챗봇 컨텍스트 없음");

      // 요약 정보 추출 (summary 필드가 있으면 사용, 없으면 기본값)
      log.debug("=== summary 필드 처리 ===");
      JsonNode summaryNode = analysis.get("summary");
      log.debug("summaryNode: {}", summaryNode);
      log.debug("summaryNode.isNull(): {}", summaryNode != null ? summaryNode.isNull() : "null");
      log.debug("summaryNode.isArray(): {}", summaryNode != null ? summaryNode.isArray() : "null");
      log.debug("summaryNode.isTextual(): {}", summaryNode != null ? summaryNode.isTextual() : "null");

      String summary = "요약 정보 없음";
      if (summaryNode != null && !summaryNode.isNull()) {
        if (summaryNode.isArray()) {
          // 배열인 경우 문자열로 결합
          StringBuilder summaryBuilder = new StringBuilder();
          for (JsonNode item : summaryNode) {
            if (item.isTextual()) {
              summaryBuilder.append(item.asText()).append(" ");
            }
          }
          summary = summaryBuilder.toString().trim();
          log.debug("summary (배열에서 결합): {}", summary);
        } else if (summaryNode.isTextual()) {
          // 문자열인 경우 그대로 사용
          summary = summaryNode.asText();
          log.debug("summary (문자열): {}", summary);
        }
      }

      log.debug("최종 summary: {}", summary);

      return Report.builder()
          .user(user)
          .title(newsData.getTitle())
          .category(category)
          .oneLineSummary(oneLineSummary)
          .url(url)
          .publisher(source)
          .publicationDate(parsePublicationDate(newsData.getDate()))
          .summary(summary)
          .chatbotContext(chatbotContext)
          .articleContent(newsData.getContent())
          .build();
    } catch (Exception e) {
      log.error("분석 결과를 Report로 변환 실패: {}", e.getMessage(), e);
      log.error("분석 결과 원본: {}", analysisResult);
      throw new ReportHandler(ReportErrorStatus.REPORT_CONVERSION_FAILED);
    }
  }

  public TrueScore convertToTrueScore(String analysisResult) {
    try {
      JsonNode analysis = objectMapper.readTree(analysisResult);
      JsonNode reliabilityAnalysis = analysis.get("reliability_analysis");

      int sourceReliability = 0;
      int factualBasis = 0;
      int adExaggeration = 0;
      int bias = 0;
      int articleStructure = 0;

      if (reliabilityAnalysis != null && reliabilityAnalysis.isArray()) {
        for (JsonNode item : reliabilityAnalysis) {
          String categoryName = getStringValue(item, "category_name", "");
          int score = getIntValue(item, "score", 0);

          switch (categoryName) {
            case "출처 신뢰성":
              sourceReliability = score;
              break;
            case "사실 근거":
              factualBasis = score;
              break;
            case "광고/과장 표현":
              adExaggeration = score;
              break;
            case "편향성":
              bias = score;
              break;
            case "기사 형식":
              articleStructure = score;
              break;
          }
        }
      }

      int overallScore = getIntValue(analysis, "total_score", 0);

      return TrueScore.builder()
          .sourceReliability(sourceReliability)
          .factualBasis(factualBasis)
          .adExaggeration(adExaggeration)
          .bias(bias)
          .articleStructure(articleStructure)
          .overallScore(overallScore)
          .build();
    } catch (Exception e) {
      log.error("분석 결과를 TrueScore로 변환 실패: {}", e.getMessage(), e);
      log.error("분석 결과 원본: {}", analysisResult);
      throw new ReportHandler(ReportErrorStatus.REPORT_CONVERSION_FAILED);
    }
  }

  public List<ReportBadge> convertToReportBadges(String analysisResult) {
    try {
      JsonNode analysis = objectMapper.readTree(analysisResult);
      List<ReportBadge> reportBadges = new ArrayList<>();

      JsonNode aiBadgesNode = analysis.get("ai_badges");
      if (aiBadgesNode != null && aiBadgesNode.isArray()) {
        for (JsonNode badgeNode : aiBadgesNode) {
          String badgeName = badgeNode.asText();
          Badge.BadgeName badgeEnum = convertBadgeName(badgeName);

          Badge badge = Badge.builder()
              .badgeName(badgeEnum)
              .build();

          ReportBadge reportBadge = ReportBadge.builder()
              .badge(badge)
              .build();

          reportBadges.add(reportBadge);
        }
      }

      return reportBadges;
    } catch (Exception e) {
      log.error("분석 결과를 ReportBadge로 변환 실패: {}", e.getMessage(), e);
      log.error("분석 결과 원본: {}", analysisResult);
      throw new ReportHandler(ReportErrorStatus.REPORT_CONVERSION_FAILED);
    }
  }

  private Badge.BadgeName convertBadgeName(String badgeName) {
    switch (badgeName) {
      case "공신력 있는 출처":
        return Badge.BadgeName.RELIABLE_SOURCE;
      case "균형 잡힌 기사":
        return Badge.BadgeName.BALANCED_ARTICLE;
      case "주의 환기 우수":
        return Badge.BadgeName.EXCELLENT_WARNING;
      case "부분적인 신뢰 가능":
        return Badge.BadgeName.PARTIALLY_TRUSTWORTHY;
      case "전문가 인용 없음":
        return Badge.BadgeName.NO_EXPERT_CITATION;
      case "광고성 기사":
        return Badge.BadgeName.ADVERTORIAL_ARTICLE;
      case "사실 검증 불가":
        return Badge.BadgeName.FACT_VERIFICATION_IMPOSSIBLE;
      case "신뢰 불가":
        return Badge.BadgeName.UNTRUSTWORTHY;
      case "광고 목적":
        return Badge.BadgeName.ADVERTISING_PURPOSE;
      case "과장 표현 다수":
        return Badge.BadgeName.MANY_EXAGGERATED_EXPRESSIONS;
      default:
        return Badge.BadgeName.RELIABLE_SOURCE; // 기본값
    }
  }

  private LocalDate parsePublicationDate(String dateStr) {
    try {
      if (dateStr == null || dateStr.equals("날짜 정보 없음")) {
        return LocalDate.now();
      }

      // 다양한 날짜 형식 처리
      String cleanDate = dateStr.trim();

      // "2025.08.05" 형식 처리
      if (cleanDate.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) {
        String[] parts = cleanDate.split("\\.");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
      }

      // 기존 로직 (ISO 형식)
      if (cleanDate.length() >= 10) {
        return LocalDate.parse(cleanDate.substring(0, 10));
      }

      return LocalDate.now();
    } catch (Exception e) {
      log.warn("날짜 파싱 실패 - dateStr: {}, 에러: {}", dateStr, e.getMessage());
      return LocalDate.now();
    }
  }

  // 안전한 문자열 값 추출을 위한 헬퍼 메서드
  private String getStringValue(JsonNode node, String fieldName, String defaultValue) {
    JsonNode fieldNode = node.get(fieldName);
    if (fieldNode != null && !fieldNode.isNull()) {
      return fieldNode.asText();
    }
    return defaultValue;
  }

  // 안전한 정수 값 추출을 위한 헬퍼 메서드
  private int getIntValue(JsonNode node, String fieldName, int defaultValue) {
    JsonNode fieldNode = node.get(fieldName);
    if (fieldNode != null && !fieldNode.isNull()) {
      return fieldNode.asInt();
    }
    return defaultValue;
  }
}