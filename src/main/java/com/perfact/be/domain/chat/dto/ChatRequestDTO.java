package com.perfact.be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 요청 DTO")
public class ChatRequestDTO {

  @Schema(description = "사용자 질문", example = "왜 총점이 85점인가요?")
  private String userInput;
}