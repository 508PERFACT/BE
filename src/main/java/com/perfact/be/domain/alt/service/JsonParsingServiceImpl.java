package com.perfact.be.domain.alt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.chat.converter.JsonConverter;
import com.perfact.be.domain.report.util.ClovaResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// JSON 파싱 서비스 구현체
// 다른 도메인들의 로직을 의존성 주입으로 재사용
@Slf4j
@Service
@RequiredArgsConstructor
public class JsonParsingServiceImpl implements JsonParsingService {

  private final ObjectMapper objectMapper;
  private final ClovaResponseParser clovaResponseParser; // report 도메인 주입
  private final JsonConverter jsonConverter; // chat 도메인 주입

  @Override
  public String removeMarkdownCodeBlocks(String content) {
    if (content == null) {
      return "";
    }

    String jsonContent = content;
    if (jsonContent.startsWith("```json")) {
      jsonContent = jsonContent.substring(7);
    }
    if (jsonContent.endsWith("```")) {
      jsonContent = jsonContent.substring(0, jsonContent.length() - 3);
    }

    return jsonContent.trim();
  }

  @Override
  public JsonNode parseJson(String jsonString) {
    try {
      String cleanedJson = removeMarkdownCodeBlocks(jsonString);

      return objectMapper.readTree(cleanedJson);
    } catch (Exception e) {
      log.error("JSON 파싱 실패: {}", e.getMessage(), e);
      throw new RuntimeException("JSON 파싱 실패", e);
    }
  }

  @Override
  public List<String> parseJsonArray(String content) {
    // chat 도메인의 JsonConverter 로직 재사용
    return jsonConverter.parseJsonArray(content);
  }

  @Override
  public String getStringValue(JsonNode node, String fieldName, String defaultValue) {
    if (node == null) {
      return defaultValue;
    }
    JsonNode fieldNode = node.get(fieldName);
    return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : defaultValue;
  }

  @Override
  public int getIntValue(JsonNode node, String fieldName, int defaultValue) {
    if (node == null) {
      return defaultValue;
    }
    JsonNode fieldNode = node.get(fieldName);
    return fieldNode != null && !fieldNode.isNull() ? fieldNode.asInt() : defaultValue;
  }

  @Override
  public JsonNode getArrayNode(JsonNode node, String fieldName) {
    if (node == null) {
      return null;
    }
    JsonNode fieldNode = node.get(fieldName);
    return fieldNode != null && fieldNode.isArray() ? fieldNode : null;
  }

  @Override
  public String extractCategory(String content) {
    // report 도메인의 ClovaResponseParser 로직 재사용
    return clovaResponseParser.extractCategory(content);
  }

  @Override
  public String extractSummary(String content) {
    // report 도메인의 ClovaResponseParser 로직 재사용
    return clovaResponseParser.extractSummary(content);
  }
}
