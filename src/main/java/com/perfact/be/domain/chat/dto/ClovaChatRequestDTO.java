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
@Schema(description = "Clova 채팅 API 요청 DTO")
public class ClovaChatRequestDTO {

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
  @Schema(description = "Clova API 메시지")
  public static class Message {
    @Schema(description = "메시지 역할", example = "user", allowableValues = { "system", "user", "assistant" })
    private String role;

    @Schema(description = "메시지 내용", example = "안녕하세요")
    private String content;
  }
}