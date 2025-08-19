package com.perfact.be.domain.chat.converter;

import com.perfact.be.domain.chat.dto.ChatResponse;
import com.perfact.be.domain.chat.entity.ChatLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatLogConverter {
  // reportId, List<ChatLong> -> ChatLogListResponseDTO
  public ChatResponse.ChatLogListResponseDTO toResponseDto(Long reportId, List<ChatLog> chatLogs) {
    List<ChatResponse.ChatLogDTO> chatLogDTOs = chatLogs.stream()
        .map(this::toChatLogDto)
        .collect(Collectors.toList());

    ChatResponse.ChatLogListResponseDTO result = ChatResponse.ChatLogListResponseDTO.builder()
        .reportId(reportId)
        .chatLogs(chatLogDTOs)
        .build();

    return result;
  }

  // ChatLogEntity -> ChatLogDTO
  private ChatResponse.ChatLogDTO toChatLogDto(ChatLog chatLog) {
    return ChatResponse.ChatLogDTO.builder()
        .chatId(chatLog.getChatId())
        .senderType(chatLog.getSenderType())
        .message(chatLog.getMessage())
        .createdAt(chatLog.getCreatedAt())
        .build();
  }

  // String -> ChatResponseDTO
  public ChatResponse.ChatResponseDTO toCharResponseDto(String polishedResponse) {
    return ChatResponse.ChatResponseDTO.builder()
        .aiResponse(polishedResponse)
        .build();
  }

  // List<String> -> RecommendQuestionsResponseDTO
  public ChatResponse.RecommendQuestionsResponseDTO toRecommendQuestionsResponseDto(List<String> questions) {
    return ChatResponse.RecommendQuestionsResponseDTO.builder()
        .questions(questions)
        .build();
  }

}