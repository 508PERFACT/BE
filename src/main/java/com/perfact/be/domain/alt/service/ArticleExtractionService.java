package com.perfact.be.domain.alt.service;

import com.perfact.be.domain.alt.dto.ArticleExtractionResult;

public interface ArticleExtractionService {

  // 기사 본문 추출
  String extractArticleContent(String url);

  // 기사 제목, 날짜, 본문 함께 추출
  ArticleExtractionResult extractArticleWithMetadata(String url);
}
