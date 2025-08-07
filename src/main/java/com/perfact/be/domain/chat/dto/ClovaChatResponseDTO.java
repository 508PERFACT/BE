package com.perfact.be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Clova 채팅 API 응답 DTO")
public class ClovaChatResponseDTO {

  private Status status;
  private Result result;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Status {
    private String code;
    private String message;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Result {
    private Message message;
    private int inputLength;
    private int outputLength;
    private String stopReason;
    private Long seed;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {
    private String role;
    private String content;
  }
}