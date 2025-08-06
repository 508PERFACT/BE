package com.perfact.be.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Clova API 응답 DTO")
public class ClovaResponseDTO {

  @Schema(description = "API 응답 상태 정보")
  private Status status;

  @Schema(description = "API 응답 결과")
  private Result result;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "API 응답 상태")
  public static class Status {
    @Schema(description = "응답 코드", example = "200")
    private String code;

    @Schema(description = "응답 메시지", example = "OK")
    private String message;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "API 응답 결과")
  public static class Result {
    @Schema(description = "AI 응답 메시지")
    private Message message;

    @Schema(description = "완료 이유", example = "stop")
    private String finishReason;

    @Schema(description = "생성 시간", example = "1642234567")
    private long created;

    @Schema(description = "시드 값", example = "12345")
    private long seed;

    @Schema(description = "토큰 사용량")
    private Usage usage;

    @Schema(description = "AI 필터 정보")
    private List<AiFilter> aiFilter;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "AI 응답 메시지")
  public static class Message {
    @Schema(description = "메시지 역할", example = "assistant")
    private String role;

    @Schema(description = "메시지 내용", example = "{\"field\": \"정치\", \"summary\": \"기사 요약...\"}")
    private String content;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "토큰 사용량")
  public static class Usage {
    @Schema(description = "프롬프트 토큰 수", example = "100")
    private int promptTokens;

    @Schema(description = "완료 토큰 수", example = "200")
    private int completionTokens;

    @Schema(description = "총 토큰 수", example = "300")
    private int totalTokens;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "AI 필터 정보")
  public static class AiFilter {
    @Schema(description = "필터 그룹명", example = "content_filter")
    private String groupName;

    @Schema(description = "필터명", example = "hate")
    private String name;

    @Schema(description = "필터 점수", example = "0.1")
    private String score;

    @Schema(description = "필터 결과", example = "pass")
    private String result;
  }
}