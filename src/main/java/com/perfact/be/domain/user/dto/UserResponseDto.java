package com.perfact.be.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResponseDto {

  @Getter
  @AllArgsConstructor
  @Schema(description = "구독 상태 응답 DTO")
  public static class SubscribeStatusResponse {

    @Schema(description = "플랜 이름", example = "FREE")
    private String planName;

    @Schema(description = "구독 상태 설명", example = "무료 플랜 사용 중")
    private String subscribeStatus;

    @Schema(description = "다음 결제일", example = "무료 플랜 사용 중")
    private String nextBillingDate;

    @Schema(description = "일일 크레딧 제공량", example = "3")
    private Long dailyCredit;

    @Schema(description = "오늘 사용한 크레딧 수", example = "2")
    private Long todayUsage;

    @Schema(description = "이번 달 사용한 크레딧 수", example = "15")
    private Long thisMonthUsage;
  }

  @Getter
  @AllArgsConstructor
  @Schema(description = "닉네임 응답 DTO")
  public static class NicknameResponse {

    @Schema(description = "사용자 닉네임", example = "인고사")
    private String nickname;
  }

}
