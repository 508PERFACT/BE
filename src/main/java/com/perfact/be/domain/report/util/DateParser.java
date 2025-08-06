package com.perfact.be.domain.report.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class DateParser {

  // 날짜 문자열을 LocalDate로 변환
  public LocalDate parsePublicationDate(String dateStr) {
    try {
      if (dateStr == null || dateStr.equals("날짜 정보 없음")) {
        return LocalDate.now();
      }
      return LocalDate.parse(dateStr.substring(0, 10));
    } catch (Exception e) {
      log.warn("날짜 파싱 실패 - dateStr: {}, 에러: {}", dateStr, e.getMessage());
      return LocalDate.now();
    }
  }
}