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
@Schema(description = "추천 질문 응답 DTO")
public class RecommendQuestionsResponseDTO {

  @Schema(description = "추천 질문 목록", example = "[\"해당 기사의 출처인 언론사가 어디인지 알 수 있을까요?\", \"기업의 전략적 배경과 의도를 자세하게 설명했는데, 이 부분이 편향성 측면에서 어떤 영향을 미쳤다고 생각하시나요?\"]")
  private List<String> questions;
}