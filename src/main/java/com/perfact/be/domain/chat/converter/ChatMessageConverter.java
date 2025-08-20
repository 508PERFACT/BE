package com.perfact.be.domain.chat.converter;

import com.perfact.be.domain.chat.exception.ChatHandler;
import com.perfact.be.domain.chat.exception.status.ChatErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessageConverter {

  // AI 응답 메시지를 다듬어서 반환
  public String polishMessage(String rawMessage) {
    if (rawMessage == null || rawMessage.trim().isEmpty()) {
      throw new ChatHandler(ChatErrorStatus.CHAT_MESSAGE_PARSING_FAILED);
    }

    try {
      // 앞뒤 공백 제거
      String polished = rawMessage.trim()
          .replaceAll("\n{3,}", "\n\n")
          .replaceAll("([.!?])\n", "$1\n\n");
      log.debug("메시지 다듬기 완료 - 원본 길이: {}, 다듬은 길이: {}",
          rawMessage.length(), polished.length());
      return polished;

    } catch (Exception e) {
      log.error("메시지 다듬기 실패: {}", e.getMessage(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_MESSAGE_PARSING_FAILED);
    }

  }

}