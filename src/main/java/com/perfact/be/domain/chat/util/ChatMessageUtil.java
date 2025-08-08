package com.perfact.be.domain.chat.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessageUtil {

  // AI 응답 메시지를 다듬어서 반환
  public String polishMessage(String rawMessage) {
    try {
      if (rawMessage == null || rawMessage.trim().isEmpty()) {
        return "응답을 생성할 수 없습니다.";
      }

      // 앞뒤 공백 제거
      String polished = rawMessage.trim();

      // 연속된 개행 문자를 하나로 통일
      polished = polished.replaceAll("\n{3,}", "\n\n");

      // 문장 끝에 개행 추가하여 가독성 향상
      polished = polished.replaceAll("([.!?])\n", "$1\n\n");

      log.debug("메시지 다듬기 완료 - 원본 길이: {}, 다듬은 길이: {}",
          rawMessage.length(), polished.length());

      return polished;
    } catch (Exception e) {
      log.error("메시지 다듬기 실패: {}", e.getMessage(), e);
      return rawMessage != null ? rawMessage : "응답을 처리할 수 없습니다.";
    }
  }
}