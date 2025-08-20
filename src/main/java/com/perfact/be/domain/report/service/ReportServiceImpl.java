package com.perfact.be.domain.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.news.dto.NewsArticleResponse;
import com.perfact.be.domain.news.extractor.factory.NewsExtractorFactory;
import com.perfact.be.domain.news.service.NewsService;
import com.perfact.be.domain.report.converter.ClovaAnalysisConverter;
import com.perfact.be.domain.report.converter.ReportConverter;
import com.perfact.be.domain.report.dto.*;
import com.perfact.be.domain.report.dto.ReportResponseDto.ReportDto;
import com.perfact.be.domain.report.entity.*;
import com.perfact.be.domain.report.repository.*;
import com.perfact.be.domain.report.exception.ReportHandler;
import com.perfact.be.domain.report.exception.status.ReportErrorStatus;
import com.perfact.be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
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
  private final NewsExtractorFactory newsExtractorFactory;
  private final ReportConverter reportConverter;

  @Value("${api.clova.api-url}")
  private String CLOVA_API_URL;

  @Value("${api.clova.api-key}")
  private String CLOVA_API_KEY;

  @Override
  public Object analyzeNewsWithClova(String url) {
    try {
      // 모든 뉴스 사이트에 대해 동일한 방식으로 처리
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);
      ClovaRequestDTO request = createClovaRequest(newsData);
      ClovaResponseDTO response = callClovaAPI(request);
      return parseJsonResponse(response.getResult().getMessage().getContent());
    } catch (com.perfact.be.domain.news.exception.NewsHandler e) {
      // NewsHandler는 그대로 전달 (지원하지 않는 뉴스 사이트 등)
      log.error("뉴스 추출 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Clova API 분석 실패 - URL: {}, 에러: {}", url, e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.CLOVA_API_CALL_FAILED);
    }
  }

  @Override
  @Transactional
  public Report createReportFromAnalysis(Object analysisResult, String url, User user) {
    try {
      // 1. 뉴스 데이터 추출 - 모든 뉴스 사이트에 대해 동일한 방식으로 처리
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);

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
    } catch (com.perfact.be.domain.news.exception.NewsHandler e) {
      // NewsHandler는 그대로 전달 (지원하지 않는 뉴스 사이트 등)
      log.error("뉴스 추출 실패 - URL: {}, 사용자: {}, 에러: {}", url, user.getId(), e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("분석 결과로부터 리포트 생성 실패 - URL: {}, 사용자: {}, 에러: {}", url, user.getId(), e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.REPORT_CREATION_FAILED);
    }
  }

  @Override
  @Transactional
  public Report analyzeNewsAndCreateReport(String url, User user) {
    try {
      // 1. 뉴스 데이터 추출 - 모든 뉴스 사이트에 대해 동일한 방식으로 처리
      NewsArticleResponse newsData = newsExtractorFactory.extractNews(url);

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
    } catch (com.perfact.be.domain.news.exception.NewsHandler e) {
      // NewsHandler는 그대로 전달 (지원하지 않는 뉴스 사이트 등)
      log.error("뉴스 추출 실패 - URL: {}, 사용자: {}, 에러: {}", url, user.getId(), e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("리포트 생성 실패 - URL: {}, 사용자: {}, 에러: {}", url, user.getId(), e.getMessage(), e);
      throw new ReportHandler(ReportErrorStatus.REPORT_CREATION_FAILED);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ReportResponseDto.ReportListDto getSavedReports(User loginUser, int page) {
    if (page < 1) {
      throw new ReportHandler(ReportErrorStatus.INVALID_PAGE_NUMBER);
    }

    Pageable pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Report> reportsPage = reportRepository.findByUserOrderByCreatedAtDesc(loginUser, pageable);
    return reportConverter.toListDto(reportsPage);
  }

  @Override
  @Transactional(readOnly = true)
  public ReportResponseDto getReport(User loginUser, Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new ReportHandler(ReportErrorStatus.REPORT_NOT_FOUND));

    if (!report.getUser().getId().equals(loginUser.getId())) {
      throw new ReportHandler(ReportErrorStatus.FORBIDDEN_REPORT_ACCESS);
    }

    TrueScore trueScore = trueScoreRepository.findByReportId(reportId)
        .orElse(null);
    List<ReportBadge> reportBadges = reportBadgeRepository.findByReportId(reportId);

    return ReportResponseDto.from(report, trueScore, reportBadges);
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

      // 쌍따옴표로 묶인 JSON 문자열 처리
      if (jsonContent.startsWith("\"") && jsonContent.endsWith("\"")) {
        // 완전히 쌍따옴표로 감싸진 경우
        jsonContent = jsonContent.substring(1, jsonContent.length() - 1);
        jsonContent = jsonContent.replace("\\\"", "\"").replace("\\\\", "\\");
        log.debug("완전히 쌍따옴표로 감싸진 JSON 처리 후: {}", jsonContent);
      } else if (jsonContent.endsWith("\"")) {
        // 끝에만 쌍따옴표가 있는 경우
        jsonContent = jsonContent.substring(0, jsonContent.length() - 1);
        log.debug("끝 쌍따옴표 제거 후 JSON: {}", jsonContent);
      }

      // JSON 구조 검증
      log.debug("=== JSON 구조 검증 ===");
      log.debug("처리된 JSON 내용 길이: {}", jsonContent.length());
      log.debug("처리된 JSON 내용: {}", jsonContent);

      // JSON 구조가 올바른지 미리 검증
      if (!jsonContent.startsWith("{")) {
        log.error("JSON 구조가 올바르지 않습니다. 시작: {}",
            jsonContent.length() > 0 ? jsonContent.charAt(0) : "empty");
        throw new ReportHandler(ReportErrorStatus.ANALYSIS_RESULT_PARSING_FAILED);
      }

      // 끝 부분에서 } 찾기 (공백이나 개행 문자 제거 후)
      String trimmedContent = jsonContent.trim();
      if (!trimmedContent.endsWith("}")) {
        log.error("JSON 구조가 올바르지 않습니다. 끝: {}",
            trimmedContent.length() > 0 ? trimmedContent.charAt(trimmedContent.length() - 1) : "empty");
        // 마지막 } 찾기 시도
        int lastBraceIndex = trimmedContent.lastIndexOf("}");
        if (lastBraceIndex > 0) {
          jsonContent = trimmedContent.substring(0, lastBraceIndex + 1);
          log.debug("마지막 } 위치에서 잘라서 처리: {}", jsonContent);
        } else {
          throw new ReportHandler(ReportErrorStatus.ANALYSIS_RESULT_PARSING_FAILED);
        }
      }

      // JSON 파싱하여 객체로 변환 (더 강력한 인코딩 처리)
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
      mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
      mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
      mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

      // UTF-8로 명시적 인코딩하여 파싱
      Object jsonObject = mapper.readValue(jsonContent.getBytes(StandardCharsets.UTF_8), Object.class);

      log.debug("JSON 파싱 성공");
      return jsonObject;
    } catch (Exception e) {
      log.error("JSON 파싱 실패 - 원본 내용: {}", analysisResult);
      log.error("JSON 파싱 실패 - 에러: {}", e.getMessage(), e);

      // 파싱 실패 시 더 유연한 파싱 시도
      try {
        log.warn("기본 파싱 실패, 더 유연한 파싱 시도");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        // JSON 문자열에서 불필요한 문자 제거
        String cleanedJson = analysisResult
            .replaceAll("\\s+", " ")
            .replaceAll(",\\s*}", "}")
            .replaceAll(",\\s*]", "]");

        // 쌍따옴표로 묶인 JSON 문자열 처리
        if (cleanedJson.startsWith("\"") && cleanedJson.endsWith("\"")) {
          // 완전히 쌍따옴표로 감싸진 경우
          cleanedJson = cleanedJson.substring(1, cleanedJson.length() - 1);
          cleanedJson = cleanedJson.replace("\\\"", "\"").replace("\\\\", "\\");
        } else if (cleanedJson.endsWith("\"")) {
          // 끝에만 쌍따옴표가 있는 경우
          cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 1);
        }

        Object jsonObject = mapper.readValue(cleanedJson, Object.class);
        log.debug("유연한 파싱 성공");
        return jsonObject;
      } catch (Exception e2) {
        log.error("유연한 파싱도 실패: {}", e2.getMessage());
        throw new ReportHandler(ReportErrorStatus.ANALYSIS_RESULT_PARSING_FAILED);
      }
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
        1024,
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
      headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", generateRequestId());

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

  // 요청 ID 생성, 옵셔널한 파라미터라 일단 생성은 하지만 추후 필요하다고 판단 시 저장하여 로깅 작업 추가
  private String generateRequestId() {
    return java.util.UUID.randomUUID().toString().replace("-", "");
  }
}
