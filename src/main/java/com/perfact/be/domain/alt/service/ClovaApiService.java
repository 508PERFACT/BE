package com.perfact.be.domain.alt.service;

import com.perfact.be.domain.alt.dto.AlternativeArticleResponseDto;

// Clova API 호출을 위한 서비스 인터페이스
public interface ClovaApiService {

  // 검색어 생성을 위한 Clova API 호출
  String generateSearchQuery(String articleContent);

  // 비교 분석을 위한 Clova API 호출
  String performComparisonAnalysis(String originalContent, String opposingContent);

  // 분석 결과를 DTO로 변환
  AlternativeArticleResponseDto parseAnalysisResult(String analysisResult);
}
