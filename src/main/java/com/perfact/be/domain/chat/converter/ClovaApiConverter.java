package com.perfact.be.domain.chat.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.chat.dto.ClovaChatRequestDTO;
import com.perfact.be.domain.chat.dto.ClovaChatResponseDTO;
import com.perfact.be.domain.chat.dto.ClovaRecommendRequestDTO;
import com.perfact.be.domain.chat.dto.ClovaRecommendResponseDTO;
import com.perfact.be.domain.chat.exception.ChatHandler;
import com.perfact.be.domain.chat.exception.status.ChatErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaApiConverter {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${api.clova.chat-url}")
  private String CLOVA_CHAT_URL;

  @Value("${api.clova.api-key}")
  private String CLOVA_API_KEY;

  // 채팅 API 호출을 위한 요청을 생성
  public ClovaChatRequestDTO createChatRequest(String chatbotContext, String articleContent, String userInput) {
    List<ClovaChatRequestDTO.Message> messages = new ArrayList<>();
    messages.add(ClovaChatRequestDTO.Message.builder()
        .role("system")
        .content(
            "Important: The service name is 'Perfact' (P-e-r-f-a-c-t). This is the correct spelling, not 'Perfect'. You must always use the name 'Perfact' when referring to the service.\n\nYou are a friendly and helpful AI assistant for the 'Perfact' service. Your role is to answer user questions by referring to both the original news article (`기사 원문`) and the provided AI analysis report summary (`AI 분석 리포트 요약`). Use the original article as the primary source for facts and the analysis report for context about reliability. Do not make up information. If the answer is not in the provided context, say you don't know. Answer in Korean.")
        .build());

    // 사용자 메시지 (기사 원문 + 분석 리포트 요약 + 질문)
    String userContent = "[분석 대상 기사 원문]\n" + articleContent + "\n\n---\n\n[AI 분석 리포트 요약]\n" + chatbotContext
        + "\n\n이 내용을 기반으로 사용자의 질문에 상세히 답변하세요.\n\n---\n\n[사용자 질문]\n" + userInput;
    messages.add(ClovaChatRequestDTO.Message.builder()
        .role("user")
        .content(userContent)
        .build());

    return ClovaChatRequestDTO.builder()
        .messages(messages)
        .topP(0.8)
        .topK(0)
        .maxTokens(512)
        .temperature(0.5)
        .repeatPenalty(5.0)
        .stopBefore(new ArrayList<>())
        .includeAiFilters(true)
        .seed(0)
        .build();
  }

  // 추천 질문 API 호출을 위한 요청을 생성
  public ClovaRecommendRequestDTO createRecommendRequest(String chatbotContext) {
    List<ClovaRecommendRequestDTO.Message> messages = new ArrayList<>();

    // 시스템 메시지
    messages.add(ClovaRecommendRequestDTO.Message.builder()
        .role("system")
        .content(
            "You are an AI assistant that creates insightful recommended questions. Based on the provided analysis report summary, your goal is to generate two concise and relevant questions a user would most likely ask. The questions should highlight potential weaknesses or interesting points from the report (e.g., low scores, specific badges). Your final output MUST be a JSON array containing exactly two strings, like [\"질문 1\", \"질문 2\"]. Answer in Korean.")
        .build());

    // 사용자 메시지 (리포트 컨텍스트)
    String userContent = chatbotContext + "\n\n[지시]\n이 분석 리포트에서 사용자가 가장 궁금해할 만한 핵심 질문 2개를 추천해 주세요.";
    messages.add(ClovaRecommendRequestDTO.Message.builder()
        .role("user")
        .content(userContent)
        .build());

    return ClovaRecommendRequestDTO.builder()
        .messages(messages)
        .topP(0.8)
        .topK(0)
        .maxTokens(100)
        .temperature(0.5)
        .repeatPenalty(5.0)
        .stopBefore(new ArrayList<>())
        .includeAiFilters(true)
        .seed(0)
        .build();
  }

  // Clova 채팅 API를 호출
  public ClovaChatResponseDTO callChatAPI(ClovaChatRequestDTO request) {
    try {
      log.info("Clova 채팅 API 호출");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(CLOVA_API_KEY);

      String requestBody = objectMapper.writeValueAsString(request);
      HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      ResponseEntity<ClovaChatResponseDTO> response = restTemplate.exchange(
          CLOVA_CHAT_URL,
          HttpMethod.POST,
          entity,
          ClovaChatResponseDTO.class);

      log.info("Clova 채팅 API 응답 성공");
      return response.getBody();

    } catch (Exception e) {
      log.error("Clova 채팅 API 호출 실패: {}", e.getMessage(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);
    }
  }

  // Clova 추천 질문 API를 호출
  public ClovaRecommendResponseDTO callRecommendAPI(ClovaRecommendRequestDTO request) {
    try {
      log.info("Clova 추천 질문 API 호출");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(CLOVA_API_KEY);

      HttpEntity<ClovaRecommendRequestDTO> entity = new HttpEntity<>(request, headers);

      ResponseEntity<ClovaRecommendResponseDTO> response = restTemplate.exchange(
          CLOVA_CHAT_URL,
          HttpMethod.POST,
          entity,
          ClovaRecommendResponseDTO.class);

      log.info("Clova 추천 질문 API 응답 성공");
      return response.getBody();

    } catch (Exception e) {
      log.error("Clova 추천 질문 API 호출 실패: {}", e.getMessage(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);
    }
  }
}