package com.perfact.be.domain.report.service;

import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.user.entity.User;

public interface ReportService {

  // 뉴스를 Clova API로 분석
  Object analyzeNewsWithClova(String url);

  // 리포트 생성
  Report createReport(User user, String url, Object analysisResult);
}