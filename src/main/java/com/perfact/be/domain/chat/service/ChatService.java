package com.perfact.be.domain.chat.service;

import com.perfact.be.domain.chat.dto.ChatLogListResponseDTO;
import com.perfact.be.domain.chat.dto.ChatRequestDTO;
import com.perfact.be.domain.chat.dto.ChatResponseDTO;
import com.perfact.be.domain.chat.dto.RecommendQuestionsResponseDTO;

public interface ChatService {
  ChatResponseDTO sendMessage(Long reportId, ChatRequestDTO request);

  ChatLogListResponseDTO getChatLogs(Long reportId);

  RecommendQuestionsResponseDTO getRecommendQuestions(Long reportId);
}