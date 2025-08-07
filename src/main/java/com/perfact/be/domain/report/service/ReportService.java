package com.perfact.be.domain.report.service;

import com.perfact.be.domain.report.dto.ReportResponseDto;
import com.perfact.be.domain.report.dto.ReportResponseDto.ReportDto;
import com.perfact.be.domain.report.dto.ReportResponseDto.ReportListDto;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.user.entity.User;

public interface ReportService {
  Object analyzeNewsWithClova(String url);

  Report createReportFromAnalysis(Object analysisResult, String url, User user);

  Report analyzeNewsAndCreateReport(String url, User user);

  ReportListDto getSavedReports(User loginUser, int page);

  ReportResponseDto getReport(User loginUser, Long reportId);
}