package com.perfact.be.domain.report.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClovaResponseParser {

  // Clova API 응답에서 카테고리(field) 추출
  public String extractCategory(String content) {
    try {
      if (content.contains("\"field\"")) {
        int start = content.indexOf("\"field\"") + 8;
        int end = content.indexOf("\"", start);
        if (start > 7 && end > start) {
          return content.substring(start, end);
        }
      }
      return "기타";
    } catch (Exception e) {
      log.warn("카테고리 추출 실패: {}", e.getMessage());
      return "기타";
    }
  }

  // Clova API 응답에서 요약(summary) 추출
  public String extractSummary(String content) {
    try {
      if (content.contains("\"summary\"")) {
        int start = content.indexOf("\"summary\"") + 10;
        int end = content.indexOf("]", start);
        if (start > 9 && end > start) {
          return content.substring(start, end).replaceAll("\"", "").replaceAll(",", "\n");
        }
      }
      return "";
    } catch (Exception e) {
      log.warn("요약 추출 실패: {}", e.getMessage());
      return "";
    }
  }
}