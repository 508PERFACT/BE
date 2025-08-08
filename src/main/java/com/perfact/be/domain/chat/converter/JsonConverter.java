package com.perfact.be.domain.chat.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.chat.exception.ChatHandler;
import com.perfact.be.domain.chat.exception.status.ChatErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonConverter {

  private final ObjectMapper objectMapper;

  // JSON 배열 형태의 문자열을 List<String>으로 변환
  public List<String> parseJsonArray(String content) {
    try {
      log.debug("JSON 배열 파싱 시작 - content: {}", content);

      // content가 null이거나 비어있는지 확인
      if (content == null || content.trim().isEmpty()) {
        log.error("JSON 파싱할 content가 null이거나 비어있음");
        throw new ChatHandler(ChatErrorStatus.CHAT_MESSAGE_PARSING_FAILED);
      }

      // JSON 배열 형태의 문자열을 List<String>으로 변환
      List<String> result = objectMapper.readValue(content, List.class);

      log.debug("JSON 배열 파싱 완료 - 결과 개수: {}, 결과: {}", result.size(), result);
      return result;

    } catch (Exception e) {
      log.error("JSON 배열 파싱 실패 - content: {}, 에러: {}", content, e.getMessage(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_MESSAGE_PARSING_FAILED);
    }
  }
}