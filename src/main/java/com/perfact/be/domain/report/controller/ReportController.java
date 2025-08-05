package com.perfact.be.domain.report.controller;

import com.perfact.be.domain.report.dto.AnalyzeNewsRequestDTO;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.service.ReportService;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("")
  public ApiResponse<Object> analyzeNewsWithClova(@RequestBody AnalyzeNewsRequestDTO request) {
    Object analysisResult = reportService.analyzeNewsWithClova(request.getUrl());
    return ApiResponse.onSuccess(analysisResult);
  }

  @PostMapping("/create")
  public ApiResponse<Report> createReport(@RequestBody AnalyzeNewsRequestDTO request) {
    // 실제 사용자 정보는 인증에서 가져와야 함
    User mockUser = User.builder()
        .socialId("test_user")
        .socialType(SocialType.NAVER)
        .nickname("테스트 사용자")
        .email("test@naver.com")
        .build();

    Object analysisResult = reportService.analyzeNewsWithClova(request.getUrl());
    Report report = reportService.createReport(mockUser, request.getUrl(), analysisResult);

    return ApiResponse.onSuccess(report);
  }
}