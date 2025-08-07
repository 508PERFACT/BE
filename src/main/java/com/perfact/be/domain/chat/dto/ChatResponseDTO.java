package com.perfact.be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 응답 DTO")
public class ChatResponseDTO {

  @Schema(description = "AI 응답 메시지", example = "총점이 85점인 이유는 세부 평가 근거에서 각 항목별로 받은 점수를 합산했기 때문입니다.")
  private String aiResponse;
}