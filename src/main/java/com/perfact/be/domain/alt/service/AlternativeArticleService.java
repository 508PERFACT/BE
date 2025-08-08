package com.perfact.be.domain.alt.service;

import com.perfact.be.domain.alt.dto.AlternativeArticleResponseDto;

public interface AlternativeArticleService {

  // 리포트 ID를 기반으로 대안 기사를 찾고 비교 분석 수행
  AlternativeArticleResponseDto getAlternativeArticle(Long reportId);
}
