package com.perfact.be.domain.chat.service;

import com.perfact.be.domain.chat.dto.ChatRequestDTO;
import com.perfact.be.domain.chat.dto.ChatResponse;


public interface ChatService {
  ChatResponse.ChatResponseDTO sendMessage(Long reportId, ChatRequestDTO request);

  ChatResponse.ChatLogListResponseDTO getChatLogs(Long reportId);

  ChatResponse.RecommendQuestionsResponseDTO getRecommendQuestions(Long reportId);
}