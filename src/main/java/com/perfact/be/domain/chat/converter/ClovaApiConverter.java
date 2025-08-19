package com.perfact.be.domain.chat.converter;

import com.perfact.be.domain.chat.dto.ClovaChatRequestDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ClovaApiConverter {

  private static final String SYSTEM_CHAT_PROMPT =  "Important: The service name is 'Perfact' (P-e-r-f-a-c-t). This is the correct spelling, not 'Perfect'. You must always use the name 'Perfact' when referring to the service.\n\nYou are a friendly and helpful AI assistant for the 'Perfact' service. Your role is to answer user questions by referring to both the original news article (`기사 원문`) and the provided AI analysis report summary (`AI 분석 리포트 요약`). Use the original article as the primary source for facts and the analysis report for context about reliability. Do not make up information. If the answer is not in the provided context, say you don't know. Answer in Korean.";
  private static final String SYSTEM_RECOMMEND_PROMPT = "You are an AI assistant that creates insightful recommended questions. Based on the provided analysis report summary, your goal is to generate two concise and relevant questions a user would most likely ask. The questions should highlight potential weaknesses or interesting points from the report (e.g., low scores, specific badges). Your final output MUST be a JSON array containing exactly two strings, like [\"질문 1\", \"질문 2\"]. Answer in Korean.";
  private static final double DEFAULT_TOP_P = 0.8;
  private static final int DEFAULT_TOP_K = 0;
  private static final int CHAT_MAX_TOKENS = 512;
  private static final int RECO_MAX_TOKENS = 100;
  private static final double DEFAULT_TEMPERATURE = 0.5;
  private static final double DEFAULT_REPEAT_PENALTY = 5.0;

  // 채팅 API 호출을 위한 요청을 생성
  public ClovaChatRequestDTO createChatRequest(String chatbotContext, String articleContent, String userInput) {
    // 사용자 메시지 (기사 원문 + 분석 리포트 요약 + 질문)
    String userContent = "[분석 대상 기사 원문]\n" + articleContent
        + "\n\n---\n\n[AI 분석 리포트 요약]\n" + chatbotContext
        + "\n\n이 내용을 기반으로 사용자의 질문에 상세히 답변하세요.\n\n---\n\n[사용자 질문]\n" + userInput;

    return ClovaChatRequestDTO.builder()
        .messages(
            List.of(
                ClovaChatRequestDTO.Message.builder().role("system").content(SYSTEM_CHAT_PROMPT).build(),
                ClovaChatRequestDTO.Message.builder().role("user").content(userContent).build()
            ))
        .topP(DEFAULT_TOP_P)
        .topK(DEFAULT_TOP_K)
        .maxTokens(CHAT_MAX_TOKENS)
        .temperature(DEFAULT_TEMPERATURE)
        .repeatPenalty(DEFAULT_REPEAT_PENALTY)
        .stopBefore(new ArrayList<>())
        .includeAiFilters(true)
        .seed(0)
        .build();

  }

  // 추천 질문 API 호출을 위한 요청을 생성
  public ClovaChatRequestDTO createRecommendRequest(String chatbotContext) {
    // 사용자 메시지 (리포트 컨텍스트)
    String userContent = chatbotContext + "\n\n[지시]\n이 분석 리포트에서 사용자가 가장 궁금해할 만한 핵심 질문 2개를 추천해 주세요.";

    return ClovaChatRequestDTO.builder()
        .messages(List.of(
            ClovaChatRequestDTO.Message.builder().role("system").content(SYSTEM_RECOMMEND_PROMPT).build(),
            ClovaChatRequestDTO.Message.builder().role("user").content(userContent).build()))
        .topP(DEFAULT_TOP_P)
        .topK(DEFAULT_TOP_K)
        .maxTokens(RECO_MAX_TOKENS)
        .temperature(DEFAULT_TEMPERATURE)
        .repeatPenalty(DEFAULT_REPEAT_PENALTY)
        .stopBefore(new ArrayList<>())
        .includeAiFilters(true)
        .seed(0)
        .build();
  }

}