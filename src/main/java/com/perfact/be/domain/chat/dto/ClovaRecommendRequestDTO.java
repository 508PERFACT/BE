package com.perfact.be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Clova 추천 질문 API 요청 DTO")
public class ClovaRecommendRequestDTO {

  private List<Message> messages;
  private double topP;
  private int topK;
  private int maxTokens;
  private double temperature;
  private double repeatPenalty;
  private List<String> stopBefore;
  private boolean includeAiFilters;
  private int seed;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {
    private String role;
    private String content;
  }
}