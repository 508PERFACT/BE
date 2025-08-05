package com.perfact.be.domain.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.service.NewsService;
import com.perfact.be.domain.report.dto.ClovaRequestDTO;
import com.perfact.be.domain.report.dto.ClovaResponseDTO;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.repository.ReportRepository;
import com.perfact.be.domain.report.converter.ReportConverter;
import com.perfact.be.domain.report.exception.status.ReportErrorStatus;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final NewsService newsService;
  private final ReportRepository reportRepository;
  private final ReportConverter reportConverter;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final PromptService promptService;

  @Value("${api.clova.api-url}")
  private String CLOVA_API_URL;

  @Value("${api.clova.api-key}")
  private String CLOVA_API_KEY;

  @Override
  public Object analyzeNewsWithClova(String url) {
    try {
      if (newsService.isNaverNewsDomain(url)) {
        return analyzeNaverNews(url);
      } else {
        return analyzeOtherNewsSite(url);
      }
    } catch (Exception e) {
      throw new GeneralException(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }

  private Object analyzeNaverNews(String url) {
    try {
      NewsArticleResponse newsData = newsService.extractNaverNewsArticle(url);
      ClovaRequestDTO request = createClovaRequest(newsData);
      ClovaResponseDTO response = callClovaAPI(request);
      return parseJsonResponse(response.getResult().getMessage().getContent());
    } catch (Exception e) {
      throw new GeneralException(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }

  private Object analyzeOtherNewsSite(String url) {
    try {
      String title = newsService.extractTitleFromOtherNewsSites(url);
      String content = newsService.extractNewsArticleContent(url);

      NewsArticleResponse newsData = new NewsArticleResponse(title, "날짜 정보 없음", content);
      ClovaRequestDTO request = createClovaRequest(newsData);
      ClovaResponseDTO response = callClovaAPI(request);
      return parseJsonResponse(response.getResult().getMessage().getContent());
    } catch (Exception e) {
      throw new GeneralException(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }

  private Object parseJsonResponse(String analysisResult) {
    try {
      // ```json ... ``` 형태의 마크다운 코드 블록 제거
      String jsonContent = analysisResult;
      if (jsonContent.startsWith("```json")) {
        jsonContent = jsonContent.substring(7);
      }
      if (jsonContent.endsWith("```")) {
        jsonContent = jsonContent.substring(0, jsonContent.length() - 3);
      }

      // 앞뒤 공백 제거
      jsonContent = jsonContent.trim();

      // JSON 파싱하여 객체로 변환 (UTF-8 인코딩 명시)
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
      Object jsonObject = mapper.readValue(jsonContent.getBytes("UTF-8"), Object.class);

      return jsonObject;
    } catch (Exception e) {
      // 파싱 실패 시 원본 문자열 반환
      return analysisResult;
    }
  }

  private ClovaRequestDTO createClovaRequest(NewsArticleResponse newsData) throws JsonProcessingException {
    List<ClovaRequestDTO.Message> messages = new ArrayList<>();

    messages.add(new ClovaRequestDTO.Message("system", promptService.getSystemPrompt()));

    messages.addAll(promptService.getExampleConversations());

    String newsContent = objectMapper.writeValueAsString(newsData);
    messages.add(new ClovaRequestDTO.Message("user", newsContent));

    return new ClovaRequestDTO(
        messages,
        0.8,
        0,
        2048,
        0.5,
        1.1,
        new ArrayList<>(),
        0,
        true);
  }

  private ClovaResponseDTO callClovaAPI(ClovaRequestDTO request) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(CLOVA_API_KEY);

      String requestBody = objectMapper.writeValueAsString(request);
      HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      ResponseEntity<String> response = restTemplate.exchange(
          CLOVA_API_URL,
          HttpMethod.POST,
          entity,
          String.class);

      ClovaResponseDTO responseBody = null;
      try {
        responseBody = objectMapper.readValue(response.getBody(), ClovaResponseDTO.class);
      } catch (Exception e) {
        throw new GeneralException(ReportErrorStatus.ANALYSIS_RESULT_PARSING_FAILED);
      }

      return responseBody;

    } catch (Exception e) {
      throw new GeneralException(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }

  @Override
  public Report createReport(User user, String url, Object analysisResult) {
    try {
      NewsArticleResponse newsData;
      if (newsService.isNaverNewsDomain(url)) {
        newsData = newsService.extractNaverNewsArticle(url);
      } else {
        String title = newsService.extractTitleFromOtherNewsSites(url);
        String content = newsService.extractNewsArticleContent(url);
        newsData = new NewsArticleResponse(title, "날짜 정보 없음", content);
      }

      ClovaResponseDTO clovaResponse = objectMapper.convertValue(analysisResult, ClovaResponseDTO.class);

      Report report = reportConverter.toReport(clovaResponse, newsData, user, url);

      return reportRepository.save(report);
    } catch (Exception e) {
      throw new GeneralException(ReportErrorStatus.REPORT_CREATION_FAILED);
    }
  }
}