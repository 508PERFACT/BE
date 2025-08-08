package com.perfact.be.domain.alt.exception;

import com.perfact.be.domain.alt.exception.status.AltErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class AltExceptionHandler {

  // 안전한 추출 작업 수행
  public <T> T safeExtract(String operation, Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error("{} 실패: {}", operation, e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_EXTRACTION_FAILED);
    }
  }

  // 안전한 실행 작업 수행
  public void safeExecute(String operation, Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      log.error("{} 실패: {}", operation, e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_EXTRACTION_FAILED);
    }
  }

  // 안전한 API 호출 수행
  public <T> T safeApiCall(String operation, Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error("{} 실패: {}", operation, e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
    }
  }

  // 안전한 데이터베이스 작업 수행
  public <T> T safeDatabaseOperation(String operation, Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error("{} 실패: {}", operation, e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_PERSISTENCE_FAILED);
    }
  }

  // 안전한 변환 작업 수행
  public <T> T safeConversion(String operation, Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error("{} 실패: {}", operation, e.getMessage(), e);
      throw new AltHandler(AltErrorStatus.ALT_CONVERSION_FAILED);
    }
  }

  // 추출 실패 처리
  public void handleExtractionFailure(String operation, Exception e) {
    log.error("{} 실패: {}", operation, e.getMessage(), e);
    throw new AltHandler(AltErrorStatus.ALT_EXTRACTION_FAILED);
  }

  // API 호출 실패 처리
  public void handleApiCallFailure(String operation, Exception e) {
    log.error("{} 실패: {}", operation, e.getMessage(), e);
    throw new AltHandler(AltErrorStatus.ALT_CLOVA_API_FAILED);
  }

  // 데이터베이스 작업 실패 처리
  public void handleDatabaseFailure(String operation, Exception e) {
    log.error("{} 실패: {}", operation, e.getMessage(), e);
    throw new AltHandler(AltErrorStatus.ALT_PERSISTENCE_FAILED);
  }

  // 변환 실패 처리
  public void handleConversionFailure(String operation, Exception e) {
    log.error("{} 실패: {}", operation, e.getMessage(), e);
    throw new AltHandler(AltErrorStatus.ALT_CONVERSION_FAILED);
  }
}
