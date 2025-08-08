package com.perfact.be.domain.alt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ClovaSearchRequestDto {
  private List<Message> messages;
  private double topP;
  private int topK;
  private int maxTokens;
  private double temperature;
  private double repeatPenalty;
  private List<String> stopBefore;
  private boolean includeAiFilters;
  private long seed;

  @Getter
  @Setter
  @Builder
  public static class Message {
    private String role;
    private String content;
  }
}
