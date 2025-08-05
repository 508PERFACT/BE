package com.perfact.be.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClovaRequestDTO {
  private List<Message> messages;
  private double topP;
  private int topK;
  private int maxTokens;
  private double temperature;
  private double repetitionPenalty;
  private List<String> stop;
  private int seed;
  private boolean includeAiFilters;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {
    private String role;
    private String content;
  }
}