package com.perfact.be.domain.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "뉴스 기사 응답 DTO")
public class NewsArticleResponse {

  @Schema(description = "뉴스 기사 제목", example = "AI 기술 발전으로 인한 산업 변화")
  private String title;

  @Schema(description = "뉴스 발행일", example = "2025-08-06")
  private String date;

  @Schema(description = "뉴스 기사 본문 내용", example = "최근 AI 기술의 급속한 발전으로 인해...")
  private String content;
}