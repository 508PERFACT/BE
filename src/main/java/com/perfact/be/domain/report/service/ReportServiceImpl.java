package com.perfact.be.domain.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.service.NewsService;
import com.perfact.be.domain.report.converter.ClovaAnalysisConverter;
import com.perfact.be.domain.report.dto.ClovaRequestDTO;
import com.perfact.be.domain.report.dto.ClovaResponseDTO;
import com.perfact.be.domain.report.entity.Badge;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.entity.ReportBadge;
import com.perfact.be.domain.report.entity.TrueScore;
import com.perfact.be.domain.report.repository.BadgeRepository;
import com.perfact.be.domain.report.repository.ReportBadgeRepository;
import com.perfact.be.domain.report.repository.ReportRepository;
import com.perfact.be.domain.report.repository.TrueScoreRepository;
import com.perfact.be.domain.report.exception.ReportHandler;
import com.perfact.be.domain.report.exception.status.ReportErrorStatus;
import com.perfact.be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final NewsService newsService;
  private final ReportRepository reportRepository;
  private final BadgeRepository badgeRepository;
  private final TrueScoreRepository trueScoreRepository;
  private final ReportBadgeRepository reportBadgeRepository;
  private final ClovaAnalysisConverter clovaAnalysisConverter;
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
      log.error("Clova API 분석 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }

  @Override
  @Transactional
  public Report createReportFromAnalysis(Object analysisResult, String url, User user) {
    try {
      // 1. 뉴스 데이터 추출
      NewsArticleResponse newsData;
      if (newsService.isNaverNewsDomain(url)) {
        newsData = newsService.extractNaverNewsArticle(url);
      } else {
        String title = newsService.extractTitleFromOtherNewsSites(url);
        String content = newsService.extractNewsArticleContent(url);
        newsData = new NewsArticleResponse(title, "날짜 정보 없음", content);
      }

      // 2. 분석 결과를 JSON 문자열로 변환
      log.debug("분석 결과 객체 타입: {}", analysisResult.getClass().getSimpleName());
      String analysisResultJson = objectMapper.writeValueAsString(analysisResult);
      log.debug("변환된 JSON 문자열: {}", analysisResultJson);

      // 3. Report 엔티티 생성 및 저장
      Report report = clovaAnalysisConverter.convertToReport(analysisResultJson, newsData, user, url);
      Report savedReport = reportRepository.save(report);

      // 4. TrueScore 엔티티 생성 및 저장
      TrueScore trueScore = clovaAnalysisConverter.convertToTrueScore(analysisResultJson);
      trueScore.setReportId(savedReport.getReportId());
      TrueScore savedTrueScore = trueScoreRepository.save(trueScore);

      // 5. ReportBadge 엔티티들 생성 및 저장
      List<ReportBadge> reportBadges = clovaAnalysisConverter.convertToReportBadges(analysisResultJson);
      for (ReportBadge reportBadge : reportBadges) {
        // Badge가 이미 존재하는지 확인하고 없으면 저장
        Badge badge = reportBadge.getBadge();
        Badge savedBadge = badgeRepository.findByBadgeName(badge.getBadgeName())
            .orElseGet(() -> badgeRepository.save(badge));

        reportBadge.setBadge(savedBadge);
        reportBadge.setReportId(savedReport.getReportId());
        ReportBadge savedReportBadge = reportBadgeRepository.save(reportBadge);
      }

      return savedReport;
    } catch (Exception e) {
      log.error("분석 결과로부터 리포트 생성 실패 - URL: {}, 사용자: {}, 에러: {}", url, user.getId(), e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.REPORT_CREATION_FAILED);
    }
  }

  @Override
  @Transactional
  public Report analyzeNewsAndCreateReport(String url, User user) {
    try {
      // 1. 뉴스 데이터 추출
      NewsArticleResponse newsData;
      if (newsService.isNaverNewsDomain(url)) {
        newsData = newsService.extractNaverNewsArticle(url);
      } else {
        String title = newsService.extractTitleFromOtherNewsSites(url);
        String content = newsService.extractNewsArticleContent(url);
        newsData = new NewsArticleResponse(title, "날짜 정보 없음", content);
      }

      // 2. Clova API 분석 수행
      Object analysisResult = analyzeNewsWithClova(url);
      log.debug("분석 결과 객체 타입: {}", analysisResult.getClass().getSimpleName());
      String analysisResultJson = objectMapper.writeValueAsString(analysisResult);
      log.debug("변환된 JSON 문자열: {}", analysisResultJson);

      // 3. Report 엔티티 생성 및 저장
      Report report = clovaAnalysisConverter.convertToReport(analysisResultJson, newsData, user, url);
      Report savedReport = reportRepository.save(report);

      // 4. TrueScore 엔티티 생성 및 저장
      TrueScore trueScore = clovaAnalysisConverter.convertToTrueScore(analysisResultJson);
      trueScore.setReportId(savedReport.getReportId());
      TrueScore savedTrueScore = trueScoreRepository.save(trueScore);

      // 5. ReportBadge 엔티티들 생성 및 저장
      List<ReportBadge> reportBadges = clovaAnalysisConverter.convertToReportBadges(analysisResultJson);
      for (ReportBadge reportBadge : reportBadges) {
        // Badge가 이미 존재하는지 확인하고 없으면 저장
        Badge badge = reportBadge.getBadge();
        Badge savedBadge = badgeRepository.findByBadgeName(badge.getBadgeName())
            .orElseGet(() -> badgeRepository.save(badge));

        reportBadge.setBadge(savedBadge);
        reportBadge.setReportId(savedReport.getReportId());
        ReportBadge savedReportBadge = reportBadgeRepository.save(reportBadge);
      }

      return savedReport;
    } catch (Exception e) {
      log.error("리포트 생성 실패 - URL: {}, 사용자: {}, 에러: {}", url, user.getId(), e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.REPORT_CREATION_FAILED);
    }
  }

  private Object analyzeNaverNews(String url) {
    try {
      NewsArticleResponse newsData = newsService.extractNaverNewsArticle(url);
      ClovaRequestDTO request = createClovaRequest(newsData);
      ClovaResponseDTO response = callClovaAPI(request);
      return parseJsonResponse(response.getResult().getMessage().getContent());
    } catch (Exception e) {
      log.error("네이버 뉴스 분석 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.CLOVA_API_CALL_FAILED);
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
      log.error("기타 뉴스 사이트 분석 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }

  private Object parseJsonResponse(String analysisResult) {
    try {
      // 원본 응답 로깅
      log.debug("=== 원본 Clova 응답 ===");
      log.debug("analysisResult 길이: {}", analysisResult != null ? analysisResult.length() : "null");
      log.debug("analysisResult: {}", analysisResult);

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

      // JSON 구조 검증
      log.debug("=== JSON 구조 검증 ===");
      log.debug("처리된 JSON 내용 길이: {}", jsonContent.length());
      log.debug("처리된 JSON 내용: {}", jsonContent);

      // JSON 구조가 올바른지 미리 검증
      if (!jsonContent.startsWith("{") || !jsonContent.endsWith("}")) {
        log.error("JSON 구조가 올바르지 않습니다. 시작: {}, 끝: {}",
            jsonContent.length() > 0 ? jsonContent.charAt(0) : "empty",
            jsonContent.length() > 0 ? jsonContent.charAt(jsonContent.length() - 1) : "empty");
        throw new ReportHandler(ReportErrorStatus.ANALYSIS_RESULT_PARSING_FAILED);
      }

      // JSON 파싱하여 객체로 변환 (더 강력한 인코딩 처리)
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
      mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

      // UTF-8로 명시적 인코딩하여 파싱
      Object jsonObject = mapper.readValue(jsonContent.getBytes("UTF-8"), Object.class);

      log.debug("JSON 파싱 성공");
      return jsonObject;
    } catch (Exception e) {
      log.error("JSON 파싱 실패 - 원본 내용: {}", analysisResult);
      log.error("JSON 파싱 실패 - 에러: {}", e.getMessage(), e);

      // 파싱 실패 시 예외를 던져서 상위에서 처리하도록 함
      throw new ReportHandler(ReportErrorStatus.ANALYSIS_RESULT_PARSING_FAILED);
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
        log.error("Clova API 응답 파싱 실패: {}", e.getMessage(), e);
        throw new ReportHandler(ReportErrorStatus.ANALYSIS_RESULT_PARSING_FAILED);
      }

      return responseBody;

    } catch (Exception e) {
      log.error("Clova API 호출 실패: {}", e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }
}