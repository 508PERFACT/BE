package com.perfact.be.domain.report.controller;

import com.perfact.be.domain.report.dto.AnalyzeNewsRequestDTO;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.service.ReportService;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.global.apiPayload.ApiResponse;
import com.perfact.be.global.resolver.CurrentUser;
import io.swagger.v3.oas.annotations.Parameter;
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
  public ApiResponse<Object> analyzeNewsWithClova(
      @RequestBody AnalyzeNewsRequestDTO request) {
    Object analysisResult = reportService.analyzeNewsWithClova(request.getUrl());
    return ApiResponse.onSuccess(analysisResult);
  }

  @PostMapping("/create")
  public ApiResponse<Report> createReport(
      @RequestBody AnalyzeNewsRequestDTO request,
      @CurrentUser @Parameter(hidden=true) User loginUser) {
    Object analysisResult = reportService.analyzeNewsWithClova(request.getUrl());
    Report report = reportService.createReport(loginUser, request.getUrl(), analysisResult);

    return ApiResponse.onSuccess(report);
  }
}