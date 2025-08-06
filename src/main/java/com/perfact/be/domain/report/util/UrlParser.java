package com.perfact.be.domain.report.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UrlParser {

  // URL에서 출처(도메인) 정보 추출
  public String extractPublisher(String url) {
    try {
      String domain = url.replaceAll("https?://", "").replaceAll("www\\.", "");
      return domain.split("/")[0];
    } catch (Exception e) {
      log.warn("출처 추출 실패 - URL: {}, 에러: {}", url, e.getMessage());
      return "unknown";
    }
  }
}