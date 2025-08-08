package com.perfact.be.domain.alt.controller;

import com.perfact.be.domain.alt.dto.AlternativeArticleResponseDto;
import com.perfact.be.domain.alt.service.AlternativeArticleService;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.global.aop.CreditCheck;
import com.perfact.be.global.apiPayload.ApiResponse;
import com.perfact.be.global.resolver.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Alternative Article", description = "대안 기사 분석 API")
@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class AlternativeArticleController {

  private final AlternativeArticleService alternativeArticleService;

  @CreditCheck(cost=1)
  @Operation(summary = "대안 기사 분석", description = "리포트의 기사 내용을 기반으로 반대 관점의 기사를 찾아 비교 분석을 수행합니다.")
  @GetMapping("/{reportId}/alternative")
  public ApiResponse<AlternativeArticleResponseDto> getAlternativeArticle(
      @CurrentUser @Parameter(hidden = true) User loginUser,
      @Parameter(description = "리포트 ID", required = true, example = "1") @PathVariable Long reportId) {

    log.info("대안 기사 분석 요청 - reportId: {}", reportId);

    try {
      AlternativeArticleResponseDto response = alternativeArticleService.getAlternativeArticle(reportId);
      log.info("대안 기사 분석 완료 - reportId: {}", reportId);
      return ApiResponse.onSuccess(response);

    } catch (Exception e) {
      log.error("대안 기사 분석 실패 - reportId: {}, 에러: {}", reportId, e.getMessage(), e);
      throw e;
    }
  }
}
