package com.perfact.be.domain.chat.converter;

import com.perfact.be.domain.chat.dto.ChatLogListResponseDTO;
import com.perfact.be.domain.chat.entity.ChatLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatLogConverter {

  // ChatLog 엔티티를 ChatLogListResponseDTO로 변환
  public ChatLogListResponseDTO toResponseDto(Long reportId, List<ChatLog> chatLogs) {
    log.debug("ChatLog 변환 시작 - reportId: {}, chatLogs 개수: {}", reportId, chatLogs.size());

    List<ChatLogListResponseDTO.ChatLogDTO> chatLogDTOs = chatLogs.stream()
        .map(this::toChatLogDto)
        .collect(Collectors.toList());

    ChatLogListResponseDTO result = ChatLogListResponseDTO.builder()
        .reportId(reportId)
        .chatLogs(chatLogDTOs)
        .build();

    log.debug("ChatLog 변환 완료 - 결과: {}", result);
    return result;
  }

  // ChatLog 엔티티를 ChatLogDTO로 변환
  private ChatLogListResponseDTO.ChatLogDTO toChatLogDto(ChatLog chatLog) {
    return ChatLogListResponseDTO.ChatLogDTO.builder()
        .chatId(chatLog.getChatId())
        .senderType(chatLog.getSenderType())
        .message(chatLog.getMessage())
        .createdAt(chatLog.getCreatedAt())
        .build();
  }
}