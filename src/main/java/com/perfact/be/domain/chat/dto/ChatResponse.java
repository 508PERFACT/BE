package com.perfact.be.domain.chat.dto;

import com.perfact.be.domain.chat.entity.SenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Schema(description = "챗봇 관련 응답 DTO")
public class ChatResponse {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "채팅 응답 DTO")
  public static class ChatResponseDTO {
    @Schema(description = "AI 응답 메시지",
        example = "총점이 85점인 이유는 세부 평가 근거에서 각 항목별로 받은 점수를 합산했기 때문입니다.")
    private String aiResponse;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "추천 질문 응답 DTO")
  public static class RecommendQuestionsResponseDTO {
    @Schema(description = "추천 질문 목록", example = "[\"해당 기사의 출처인 언론사가 어디인지 알 수 있을까요?\", \"기업의 전략적 배경과 의도를 자세하게 설명했는데, 이 부분이 편향성 측면에서 어떤 영향을 미쳤다고 생각하시나요?\"]")
    private List<String> questions;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "채팅 로그 목록 응답 DTO")
  public static class ChatLogListResponseDTO {
    @Schema(description = "리포트 ID", example = "1")
    private Long reportId;

    @Schema(description = "채팅 로그 목록")
    private List<ChatLogDTO> chatLogs;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "채팅 로그 DTO")
  public static class ChatLogDTO {
    @Schema(description = "채팅 ID", example = "1")
    private Long chatId;

    @Schema(description = "발신자 타입", example = "USER", allowableValues = {"USER", "AI"})
    private SenderType senderType;

    @Schema(description = "메시지 내용", example = "안녕하세요")
    private String message;

    @Schema(description = "생성 시간", example = "2025-08-07T01:30:56")
    private LocalDateTime createdAt;
  }

}