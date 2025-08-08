package com.perfact.be.domain.alt.service;

import com.perfact.be.domain.alt.converter.AlternativeArticleConverter;
import com.perfact.be.domain.alt.dto.AlternativeArticleResponseDto;
import com.perfact.be.domain.alt.dto.ArticleExtractionResult;
import com.perfact.be.domain.alt.entity.AlternativeArticle;
import com.perfact.be.domain.alt.entity.ContentComparison;
import com.perfact.be.domain.alt.entity.PerspectiveComparison;

import com.perfact.be.domain.alt.exception.AltHandler;
import com.perfact.be.domain.alt.exception.status.AltErrorStatus;
import com.perfact.be.domain.alt.repository.AlternativeArticleRepository;
import com.perfact.be.domain.alt.repository.ContentComparisonRepository;
import com.perfact.be.domain.alt.repository.PerspectiveComparisonRepository;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlternativeArticleServiceImpl implements AlternativeArticleService {

  private final ReportRepository reportRepository;
  private final AlternativeArticleRepository alternativeArticleRepository;
  private final ContentComparisonRepository contentComparisonRepository;
  private final PerspectiveComparisonRepository perspectiveComparisonRepository;
  private final NaverSearchService naverSearchService;
  private final ArticleExtractionService articleExtractionService;

  private final ClovaApiService clovaApiService;
  private final AlternativeArticleConverter alternativeArticleConverter;

  @Override
  @Transactional
  public AlternativeArticleResponseDto getAlternativeArticle(Long reportId) {
    try {
      log.info("대안 기사 분석 요청 - reportId: {}", reportId);

      // 1. 리포트 조회 (존재하지 않는 경우 명확한 에러 처리)
      Report report = reportRepository.findById(reportId)
          .orElseThrow(() -> {
            log.error("리포트를 찾을 수 없습니다 - reportId: {}", reportId);
            return new AltHandler(AltErrorStatus.ALT_REPORT_NOT_FOUND);
          });

      // 2. 이미 분석된 결과가 있는지 확인
      var existingAlternative = alternativeArticleRepository.findByReportReportId(reportId);
      if (existingAlternative.isPresent()) {
        log.info("기존 분석 결과 사용 - reportId: {}", reportId);
        return convertToResponseDto(existingAlternative.get());
      }

      // 3. 검색어 생성
      String searchQuery = clovaApiService.generateSearchQuery(report.getArticleContent());
      log.info("생성된 검색어: {}", searchQuery);

      // 4. 네이버 뉴스 검색
      String opposingArticleUrl = naverSearchService.searchNaverNews(searchQuery);
      log.info("검색된 뉴스 URL: {}", opposingArticleUrl);

      // 5. 반대 기사 메타데이터 및 본문 추출
      ArticleExtractionResult extractionResult = articleExtractionService
          .extractArticleWithMetadata(opposingArticleUrl);
      log.info("추출된 반대 기사 제목: {}", extractionResult.getTitle());
      log.info("추출된 반대 기사 발행일: {}", extractionResult.getPublicationDate());
      log.info("추출된 반대 기사 본문 길이: {}", extractionResult.getContent().length());

      // 6. 비교 분석 수행
      String analysisResult = clovaApiService.performComparisonAnalysis(
          report.getArticleContent(), extractionResult.getContent());
      log.info("분석 결과 길이: {}", analysisResult.length());

      // 7. 분석 결과 파싱 및 저장
      AlternativeArticleResponseDto responseDto = clovaApiService.parseAnalysisResult(analysisResult);

      // 제목과 발행일을 응답 DTO에 추가
      responseDto.setOpposingTitle(extractionResult.getTitle());
      responseDto.setOpposingPublicationDate(extractionResult.getPublicationDate());

      persistAnalysisResult(report, opposingArticleUrl, extractionResult, responseDto);

      log.info("대안 기사 분석 완료 - reportId: {}", reportId);
      return responseDto;

    } catch (AltHandler e) {
      log.error("대안 기사 분석 실패 (AltHandler) - reportId: {}, 에러: {}", reportId, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("대안 기사 분석 실패 (예상치 못한 에러) - reportId: {}, 에러: {}", reportId, e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_COMPARISON_FAILED);
    }
  }

  /**
   * 분석 결과를 데이터베이스에 저장합니다.
   */
  private void persistAnalysisResult(Report report, String opposingArticleUrl,
      ArticleExtractionResult extractionResult, AlternativeArticleResponseDto responseDto) {
    try {
      // AlternativeArticle 저장
      AlternativeArticle alternativeArticle = alternativeArticleConverter.toAlternativeArticle(
          report, opposingArticleUrl, extractionResult, responseDto);
      AlternativeArticle savedAlternativeArticle = alternativeArticleRepository.save(alternativeArticle);

      // ContentComparison 저장
      for (AlternativeArticleResponseDto.ContentComparisonDto contentDto : responseDto.getContentComparisons()) {
        ContentComparison contentComparison = alternativeArticleConverter.toContentComparison(contentDto,
            savedAlternativeArticle);
        contentComparisonRepository.save(contentComparison);
      }

      // PerspectiveComparison 저장
      for (AlternativeArticleResponseDto.PerspectiveComparisonDto perspectiveDto : responseDto
          .getPerspectiveComparisons()) {
        PerspectiveComparison perspectiveComparison = alternativeArticleConverter
            .toPerspectiveComparison(perspectiveDto, savedAlternativeArticle);
        perspectiveComparisonRepository.save(perspectiveComparison);
      }

      log.info("분석 결과 저장 완료 - alternativeArticleId: {}", savedAlternativeArticle.getAlternativeArticleId());

    } catch (Exception e) {
      log.error("분석 결과 저장 실패: {}", e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_PERSISTENCE_FAILED);
    }
  }

  /**
   * AlternativeArticle 엔티티를 응답 DTO로 변환합니다.
   */
  private AlternativeArticleResponseDto convertToResponseDto(AlternativeArticle alternativeArticle) {
    try {
      List<ContentComparison> contentComparisons = contentComparisonRepository
          .findByAlternativeArticleAlternativeArticleId(alternativeArticle.getAlternativeArticleId());

      List<PerspectiveComparison> perspectiveComparisons = perspectiveComparisonRepository
          .findByAlternativeArticleAlternativeArticleId(alternativeArticle.getAlternativeArticleId());

      return alternativeArticleConverter.toResponseDto(alternativeArticle, contentComparisons,
          perspectiveComparisons);

    } catch (Exception e) {
      log.error("응답 DTO 변환 실패: {}", e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_CONVERSION_FAILED);
    }
  }
}
