package com.perfact.be.domain.alt.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

// JSON 파싱을 위한 인터페이스
public interface JsonParsingService {

  // 마크다운 코드 블록 제거
  String removeMarkdownCodeBlocks(String content);

  // JSON 문자열을 JsonNode로 파싱
  JsonNode parseJson(String jsonString);

  // JSON 배열을 List로 파싱
  List<String> parseJsonArray(String content);

  // JsonNode에서 문자열 값 안전하게 추출
  String getStringValue(JsonNode node, String fieldName, String defaultValue);

  // JsonNode에서 정수 값 안전하게 추출
  int getIntValue(JsonNode node, String fieldName, int defaultValue);

  // JsonNode에서 배열 안전하게 추출
  JsonNode getArrayNode(JsonNode node, String fieldName);

  // Clova API 응답에서 카테고리 추출
  String extractCategory(String content);

  // Clova API 응답에서 요약 추출
  String extractSummary(String content);
}
