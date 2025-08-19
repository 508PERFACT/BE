package com.perfact.be.domain.chat.service;

import com.perfact.be.domain.chat.dto.ChatRequest;
import com.perfact.be.domain.chat.dto.ChatResponse;


public interface ChatService {
  ChatResponse.ChatResponseDTO sendMessage(Long reportId, ChatRequest request);

  ChatResponse.ChatLogListResponseDTO getChatLogs(Long reportId);

  ChatResponse.RecommendQuestionsResponseDTO getRecommendQuestions(Long reportId);
}