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
  // ChatLog 엔티티를 ChatLogListResponseDTO로 변환
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

  // ChatLog 엔티티를 ChatLogDTO로 변환
  private ChatResponse.ChatLogDTO toChatLogDto(ChatLog chatLog) {
    return ChatResponse.ChatLogDTO.builder()
        .chatId(chatLog.getChatId())
        .senderType(chatLog.getSenderType())
        .message(chatLog.getMessage())
        .createdAt(chatLog.getCreatedAt())
        .build();
  }
}