package com.perfact.be.domain.chat.dto;

import com.perfact.be.domain.chat.entity.SenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 로그 목록 응답 DTO")
public class ChatLogListResponseDTO {

  @Schema(description = "리포트 ID", example = "1")
  private Long reportId;

  @Schema(description = "채팅 로그 목록")
  private List<ChatLogDTO> chatLogs;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "채팅 로그 DTO")
  public static class ChatLogDTO {
    @Schema(description = "채팅 ID", example = "1")
    private Long chatId;

    @Schema(description = "발신자 타입", example = "USER", allowableValues = { "USER", "AI" })
    private SenderType senderType;

    @Schema(description = "메시지 내용", example = "안녕하세요")
    private String message;

    @Schema(description = "생성 시간", example = "2025-08-07T01:30:56")
    private LocalDateTime createdAt;
  }

}