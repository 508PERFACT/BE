package com.perfact.be.domain.alt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class AlternativeArticleResponseDto {
  private String opposingTitle;
  private String opposingPublicationDate;
  private String opposingSummary;
  private List<ContentComparisonDto> contentComparisons;
  private List<PerspectiveComparisonDto> perspectiveComparisons;
  private String aiConclusion;

  @Getter
  @Setter
  @Builder
  public static class ContentComparisonDto {
    private String title;
    private String article;
    private String altArticle;
  }

  @Getter
  @Setter
  @Builder
  public static class PerspectiveComparisonDto {
    private String title;
    private String article;
    private String altArticle;
  }
}
