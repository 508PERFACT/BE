package com.perfact.be.domain.report.service;

import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.user.entity.User;

public interface ReportService {
  Object analyzeNewsWithClova(String url);

  Report createReportFromAnalysis(Object analysisResult, String url, User user);

  Report analyzeNewsAndCreateReport(String url, User user);
}