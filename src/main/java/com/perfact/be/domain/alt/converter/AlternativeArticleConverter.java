package com.perfact.be.domain.alt.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.perfact.be.domain.alt.dto.AlternativeArticleResponseDto;
import com.perfact.be.domain.alt.dto.ArticleExtractionResult;
import com.perfact.be.domain.alt.entity.AlternativeArticle;
import com.perfact.be.domain.alt.entity.ContentComparison;
import com.perfact.be.domain.alt.entity.PerspectiveComparison;
import com.perfact.be.domain.alt.service.JsonParsingService;
import com.perfact.be.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlternativeArticleConverter {

  private final JsonParsingService jsonParsingService;

  public AlternativeArticle toAlternativeArticle(Report report, String opposingArticleUrl,
      ArticleExtractionResult extractionResult, AlternativeArticleResponseDto responseDto) {
    return AlternativeArticle.builder()
        .report(report)
        .title(extractionResult.getTitle())
        .url(opposingArticleUrl)
        .publicationDate(parsePublicationDate(extractionResult.getPublicationDate()))
        .summary(responseDto.getOpposingSummary())
        .result(responseDto.getAiConclusion())
        .build();
  }

  public AlternativeArticleResponseDto toResponseDto(AlternativeArticle alternativeArticle,
      List<ContentComparison> contentComparisons, List<PerspectiveComparison> perspectiveComparisons) {
    List<AlternativeArticleResponseDto.ContentComparisonDto> contentComparisonDtos = contentComparisons.stream()
        .map(this::toContentComparisonDto)
        .toList();

    List<AlternativeArticleResponseDto.PerspectiveComparisonDto> perspectiveComparisonDtos = perspectiveComparisons
        .stream()
        .map(this::toPerspectiveComparisonDto)
        .toList();

    return AlternativeArticleResponseDto.builder()
        .opposingTitle(alternativeArticle.getTitle())
        .opposingPublicationDate(formatPublicationDate(alternativeArticle.getPublicationDate()))
        .opposingSummary(alternativeArticle.getSummary())
        .contentComparisons(contentComparisonDtos)
        .perspectiveComparisons(perspectiveComparisonDtos)
        .aiConclusion(alternativeArticle.getResult())
        .build();
  }

  public AlternativeArticleResponseDto fromJsonNode(JsonNode analysis) {
    String opposingSummary = jsonParsingService.getStringValue(analysis, "opposing_summary", "");

    List<AlternativeArticleResponseDto.ContentComparisonDto> contentComparisons = new ArrayList<>();
    JsonNode contentComparisonNode = jsonParsingService.getArrayNode(analysis, "content_comparison");
    if (contentComparisonNode != null) {
      for (JsonNode item : contentComparisonNode) {
        contentComparisons.add(AlternativeArticleResponseDto.ContentComparisonDto.builder()
            .title(jsonParsingService.getStringValue(item, "category", ""))
            .article(jsonParsingService.getStringValue(item, "original", ""))
            .altArticle(jsonParsingService.getStringValue(item, "opposing", ""))
            .build());
      }
    }

    List<AlternativeArticleResponseDto.PerspectiveComparisonDto> perspectiveComparisons = new ArrayList<>();
    JsonNode perspectiveComparisonNode = jsonParsingService.getArrayNode(analysis, "perspective_comparison");
    if (perspectiveComparisonNode != null) {
      for (JsonNode item : perspectiveComparisonNode) {
        perspectiveComparisons.add(AlternativeArticleResponseDto.PerspectiveComparisonDto.builder()
            .title(jsonParsingService.getStringValue(item, "category", ""))
            .article(jsonParsingService.getStringValue(item, "original", ""))
            .altArticle(jsonParsingService.getStringValue(item, "opposing", ""))
            .build());
      }
    }

    String aiConclusion = jsonParsingService.getStringValue(analysis, "ai_conclusion", "");

    return AlternativeArticleResponseDto.builder()
        .opposingSummary(opposingSummary)
        .contentComparisons(contentComparisons)
        .perspectiveComparisons(perspectiveComparisons)
        .aiConclusion(aiConclusion)
        .build();
  }

  public ContentComparison toContentComparison(AlternativeArticleResponseDto.ContentComparisonDto dto,
      AlternativeArticle article) {
    return ContentComparison.builder()
        .alternativeArticle(article)
        .title(dto.getTitle())
        .article(dto.getArticle())
        .altArticle(dto.getAltArticle())
        .build();
  }

  public PerspectiveComparison toPerspectiveComparison(AlternativeArticleResponseDto.PerspectiveComparisonDto dto,
      AlternativeArticle article) {
    return PerspectiveComparison.builder()
        .alternativeArticle(article)
        .title(dto.getTitle())
        .article(dto.getArticle())
        .altArticle(dto.getAltArticle())
        .build();
  }

  private AlternativeArticleResponseDto.ContentComparisonDto toContentComparisonDto(ContentComparison entity) {
    return AlternativeArticleResponseDto.ContentComparisonDto.builder()
        .title(entity.getTitle())
        .article(entity.getArticle())
        .altArticle(entity.getAltArticle())
        .build();
  }

  private AlternativeArticleResponseDto.PerspectiveComparisonDto toPerspectiveComparisonDto(
      PerspectiveComparison entity) {
    return AlternativeArticleResponseDto.PerspectiveComparisonDto.builder()
        .title(entity.getTitle())
        .article(entity.getArticle())
        .altArticle(entity.getAltArticle())
        .build();
  }

  private LocalDate parsePublicationDate(String dateStr) {
    if (dateStr == null || dateStr.isBlank() || dateStr.equals("날짜 정보 없음")) {
      return null;
    }
    String[] patterns = { "yyyy-MM-dd", "yyyy.MM.dd", "yyyy/MM/dd", "yyyy년 MM월 dd일" };
    for (String pattern : patterns) {
      try {
        return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern(pattern));
      } catch (Exception e) {
        // 다음 패턴 시도
      }
    }
    return null;
  }

  private String formatPublicationDate(LocalDate date) {
    if (date == null) {
      return null;
    }
    return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }
}
