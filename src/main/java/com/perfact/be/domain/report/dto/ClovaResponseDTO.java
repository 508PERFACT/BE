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
public class ClovaResponseDTO {
  private Status status;
  private Result result;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Status {
    private String code;
    private String message;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Result {
    private Message message;
    private String finishReason;
    private long created;
    private long seed;
    private Usage usage;
    private List<AiFilter> aiFilter;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {
    private String role;
    private String content;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AiFilter {
    private String groupName;
    private String name;
    private String score;
    private String result;
  }
}