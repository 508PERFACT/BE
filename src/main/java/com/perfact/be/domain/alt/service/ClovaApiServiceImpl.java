package com.perfact.be.domain.alt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.alt.converter.AlternativeArticleConverter;
import com.perfact.be.domain.alt.dto.*;
import com.perfact.be.domain.alt.exception.AltHandler;
import com.perfact.be.domain.alt.exception.status.AltErrorStatus;
import com.perfact.be.domain.alt.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClovaApiServiceImpl implements ClovaApiService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final HttpUtil httpUtil;
  private final JsonParsingService jsonParsingService;
  private final AlternativeArticleConverter alternativeArticleConverter;

  @Value("${api.clova.chat-url}")
  private String CLOVA_SEARCH_URL;

  @Value("${api.clova.api-url}")
  private String CLOVA_ANALYSIS_URL;

  @Value("${api.clova.api-key}")
  private String CLOVA_API_KEY;

  @Override
  public String generateSearchQuery(String articleContent) {
    log.info("검색어 생성 요청 - 기사 내용 길이: {}", articleContent.length());

    var request = ClovaSearchRequestDto.builder()
        .messages(List.of(
            ClovaSearchRequestDto.Message.builder()
                .role("system")
                .content(
                    "You are an AI expert specializing in information retrieval and search query formulation. Your primary goal is to read a given news article, identify its core claim, and then generate the single most effective search query to find articles with an opposing or different perspective. Your final output MUST be ONLY the search query string itself. Do not add any explanation, quotation marks, or prefixes like '검색어:'.")
                .build(),
            ClovaSearchRequestDto.Message.builder()
                .role("user")
                .content("[분석할 기사 본문]\n" + articleContent
                    + "\n\n---\n\n[지시]\n이 기사와 반대되거나 다른 시각의 뉴스를 찾기 위한 최적의 검색어를 1개만 생성해 주세요.")
                .build()))
        .topP(0.8)
        .topK(0)
        .maxTokens(100)
        .temperature(0.5)
        .repeatPenalty(5.0)
        .stopBefore(new ArrayList<>())
        .includeAiFilters(true)
        .seed(0)
        .build();

    try {
      // 요청 로깅 추가
      log.info("Clova API 요청 URL: {}", CLOVA_SEARCH_URL);

      ResponseEntity<ClovaSearchResponseDto> response = restTemplate.exchange(
          CLOVA_SEARCH_URL, HttpMethod.POST, createClovaHttpEntity(request), ClovaSearchResponseDto.class);

      log.info("Clova API 응답 상태: {}", response.getStatusCode());

      // HTTP 상태 코드 확인
      if (!response.getStatusCode().is2xxSuccessful()) {
        log.error("Clova API 에러 응답 - 상태 코드: {}, 응답: {}", response.getStatusCode(), response.getBody());
        throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
      }

      ClovaSearchResponseDto responseBody = response.getBody();
      if (responseBody == null) {
        log.error("Clova API 응답이 null입니다");
        throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
      }

      ClovaSearchResponseDto.Result result = responseBody.getResult();
      if (result == null) {
        log.error("Clova API 응답에서 result가 null입니다. 응답: {}", responseBody);
        throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
      }

      ClovaSearchResponseDto.Message message = result.getMessage();
      if (message == null) {
        log.error("Clova API 응답에서 message가 null입니다. result: {}", result);
        throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
      }

      String content = message.getContent();
      if (content == null || content.trim().isEmpty()) {
        log.error("Clova API 응답에서 content가 null이거나 비어있습니다. message: {}", message);
        throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
      }

      log.info("Clova API가 생성한 검색어: '{}'", content);

      // JSON에서 검색어 추출 시도
      try {
        JsonNode jsonNode = jsonParsingService.parseJson(content);
        String searchQuery = jsonParsingService.getStringValue(jsonNode, "search_query", "");

        if (!searchQuery.isEmpty()) {
          log.info("JSON에서 추출된 검색어: {}", searchQuery);
          return searchQuery;
        }
      } catch (Exception e) {
        log.warn("JSON 파싱 실패, 일반 텍스트로 처리: {}", e.getMessage());
      }

      // JSON 파싱 실패 시 일반 텍스트로 처리
      String searchQuery = content.trim();
      log.info("일반 텍스트로 처리된 검색어: {}", searchQuery);
      return searchQuery;

    } catch (Exception e) {
      log.error("검색어 생성 실패: {}", e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
    }
  }

  @Override
  public String performComparisonAnalysis(String originalContent, String opposingContent) {
    log.info("비교 분석 요청 - 원본 기사 길이: {}, 반대 기사 길이: {}", originalContent.length(), opposingContent.length());

    var request = ClovaAnalysisRequestDto.builder()
        .messages(List.of(
            ClovaAnalysisRequestDto.Message.builder()
                .role("system")
                .content(
                    "You are a sophisticated AI Media Analyst specializing in comparative analysis. Your primary goal is to analyze two news articles ('original_article' and 'opposing_article') with opposing viewpoints on the same topic and generate a structured, in-depth comparison report in a strict JSON format. Your final output MUST be ONLY the JSON object.\n\n# Input Article Structure\nThe user will provide a JSON object containing two keys: `original_article` and `opposing_article`.\n\n# Analysis Process & Output Instruction\n\n### Step 1: Summarize the Opposing Article\n- Analyze the `opposing_article` and create a neutral, 3-4 line summary for the `opposing_summary` field.\n\n### Step 2: Compare Content (Fixed Categories)\n- Compare both articles across the four fixed categories: `강조점` (Emphasis), `접근 방식` (Approach), `톤` (Tone), and `의도` (Intent). For the `content_comparison` field, provide a concise one-sentence analysis for both the `original` and `opposing` articles for each category.\n\n### Step 3: Compare Perspectives (AI-Generated Categories)\n- Based on the content of both articles, you MUST devise 4-5 insightful new categories for comparison. These categories should highlight the core differences in their perspectives (e.g., '감량 효과', '부작용 강조', '주요 인용 대상'). For the `perspective_comparison` field, provide a concise one-sentence analysis for both articles for each of your self-created categories.\n\n### Step 4: Formulate a Final Conclusion\n- Synthesize all the findings into a balanced, 3-4 line `ai_conclusion`. Evaluate the basis of each article's claims (e.g., user anecdotes vs. expert opinions, scientific data). Provide a final, reasoned judgment to help the user understand the complete picture and make an informed decision. Do not simply state that both are different; guide the user on which perspective might be more reliable or for whom each perspective is relevant.\n\n# Final JSON Output Structure\nYour final output MUST follow this structure precisely:\n{\n  \"opposing_summary\": \"A 3-4 line summary of the opposing article.\",\n  \"content_comparison\": [\n    {\n      \"category\": \"강조점\",\n      \"original\": \"One-sentence analysis for the original article.\",\n      \"opposing\": \"One-sentence analysis for the opposing article.\"\n    },\n    {\n      \"category\": \"접근 방식\",\n      \"original\": \"string\",\n      \"opposing\": \"string\"\n    },\n    {\n      \"category\": \"톤\",\n      \"original\": \"string\",\n      \"opposing\": \"string\"\n    },\n    {\n      \"category\": \"의도\",\n      \"original\": \"string\",\n      \"opposing\": \"string\"\n    }\n  ],\n  \"perspective_comparison\": [\n    {\n      \"category\": \"AI-generated category 1\",\n      \"original\": \"One-sentence analysis for the original article.\",\n      \"opposing\": \"One-sentence analysis for the opposing article.\"\n    }\n  ],\n  \"ai_conclusion\": \"A 3-4 line final reasoned judgment for the user.\"\n}")
                .build(),
            ClovaAnalysisRequestDto.Message.builder()
                .role("user")
                .content("{\n  \"original_article\": \"" + originalContent + "\",\n  \"opposing_article\": \""
                    + opposingContent + "\"\n}")
                .build()))
        .topP(0.8)
        .topK(0)
        .maxTokens(2048)
        .temperature(0.5)
        .repeatPenalty(5.0)
        .stopBefore(new ArrayList<>())
        .includeAiFilters(true)
        .seed(0)
        .build();

    try {
      // 요청 로깅 추가
      log.info("Clova API 요청 URL: {}", CLOVA_ANALYSIS_URL);

      ResponseEntity<ClovaAnalysisResponseDto> response = restTemplate.exchange(
          CLOVA_ANALYSIS_URL, HttpMethod.POST, createClovaHttpEntity(request), ClovaAnalysisResponseDto.class);

      log.info("Clova API 응답 상태: {}", response.getStatusCode());

      // HTTP 상태 코드 확인
      if (!response.getStatusCode().is2xxSuccessful()) {
        log.error("Clova API 에러 응답 - 상태 코드: {}, 응답: {}", response.getStatusCode(), response.getBody());
        throw new AltHandler(AltErrorStatus.ALT_COMPARISON_FAILED);
      }

      ClovaAnalysisResponseDto responseBody = response.getBody();
      if (responseBody == null) {
        log.error("Clova API 응답이 null입니다");
        throw new AltHandler(AltErrorStatus.ALT_COMPARISON_FAILED);
      }

      ClovaAnalysisResponseDto.Result result = responseBody.getResult();
      if (result == null) {
        log.error("Clova API 응답에서 result가 null입니다. 응답: {}", responseBody);
        throw new AltHandler(AltErrorStatus.ALT_COMPARISON_FAILED);
      }

      ClovaAnalysisResponseDto.Message message = result.getMessage();
      if (message == null) {
        log.error("Clova API 응답에서 message가 null입니다. result: {}", result);
        throw new AltHandler(AltErrorStatus.ALT_COMPARISON_FAILED);
      }

      String content = message.getContent();
      if (content == null || content.trim().isEmpty()) {
        log.error("Clova API 응답에서 content가 null이거나 비어있습니다. message: {}", message);
        throw new AltHandler(AltErrorStatus.ALT_COMPARISON_FAILED);
      }

      return content;

    } catch (Exception e) {
      log.error("비교 분석 실패: {}", e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_COMPARISON_FAILED);
    }
  }

  @Override
  public AlternativeArticleResponseDto parseAnalysisResult(String analysisResult) {
    try {
      JsonNode analysis = jsonParsingService.parseJson(analysisResult);
      return alternativeArticleConverter.fromJsonNode(analysis);
    } catch (Exception e) {
      log.error("분석 결과 파싱 실패: {}", e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_CONVERSION_FAILED);
    }
  }

  private HttpEntity<ClovaSearchRequestDto> createClovaHttpEntity(ClovaSearchRequestDto request) {
    HttpHeaders headers = httpUtil.createClovaHeaders(CLOVA_API_KEY);
    return new HttpEntity<>(request, headers);
  }

  private HttpEntity<ClovaAnalysisRequestDto> createClovaHttpEntity(ClovaAnalysisRequestDto request) {
    HttpHeaders headers = httpUtil.createClovaHeaders(CLOVA_API_KEY);
    return new HttpEntity<>(request, headers);
  }
}
