package com.perfact.be.domain.report.controller;

import com.perfact.be.domain.report.dto.AnalyzeNewsRequestDTO;
import com.perfact.be.domain.report.dto.ReportResponseDto;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.entity.ReportBadge;
import com.perfact.be.domain.report.entity.TrueScore;
import com.perfact.be.domain.report.repository.ReportBadgeRepository;
import com.perfact.be.domain.report.repository.TrueScoreRepository;
import com.perfact.be.domain.report.service.ReportService;
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

import java.util.List;

@Tag(name = "Report", description = "리포트 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;
  private final TrueScoreRepository trueScoreRepository;
  private final ReportBadgeRepository reportBadgeRepository;

  @Operation(summary = "뉴스 기사 AI 분석 및 리포트 생성", description = "뉴스 URL을 입력받아 Clova API를 통해 AI 분석을 수행하고 리포트를 생성하여 저장합니다.")
  @PostMapping("")
  @CreditCheck(cost=1)
  public ApiResponse<ReportResponseDto> analyzeNewsAndCreateReport(
      @Parameter(description = "분석할 뉴스 URL", required = true, example = "https://news.naver.com/main/read.naver?mode=LSD&mid=shm&sid1=100&oid=001&aid=0012345678") @RequestBody AnalyzeNewsRequestDTO request,
      @CurrentUser @Parameter(hidden = true) User loginUser) {
    log.info("뉴스 분석 요청 - URL: {}", request.getUrl());

    // 1. Clova API를 통한 뉴스 분석
    Object analysisResult = reportService.analyzeNewsWithClova(request.getUrl());

    // 2. 분석 결과를 바탕으로 리포트 생성 및 저장
    Report report = reportService.createReportFromAnalysis(analysisResult, request.getUrl(), loginUser);

    // 3. 관련 데이터 조회
    TrueScore trueScore = trueScoreRepository.findByReportId(report.getReportId()).orElse(null);
    List<ReportBadge> reportBadges = reportBadgeRepository.findByReportId(report.getReportId());

    // 4. DTO로 변환하여 반환
    ReportResponseDto responseDto = ReportResponseDto.from(report, trueScore, reportBadges);

    return ApiResponse.onSuccess(responseDto);
  }
}