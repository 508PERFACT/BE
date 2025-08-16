package com.perfact.be.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.chat.converter.ChatLogConverter;
import com.perfact.be.domain.chat.converter.ClovaApiConverter;
import com.perfact.be.domain.chat.converter.JsonConverter;
import com.perfact.be.domain.chat.dto.ChatLogListResponseDTO;
import com.perfact.be.domain.chat.dto.ChatRequestDTO;
import com.perfact.be.domain.chat.dto.ChatResponseDTO;
import com.perfact.be.domain.chat.dto.RecommendQuestionsResponseDTO;
import com.perfact.be.domain.chat.entity.ChatLog;
import com.perfact.be.domain.chat.entity.SenderType;
import com.perfact.be.domain.chat.exception.ChatHandler;
import com.perfact.be.domain.chat.exception.status.ChatErrorStatus;
import com.perfact.be.domain.chat.repository.ChatLogRepository;
import com.perfact.be.domain.chat.util.ChatMessageUtil;
import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatLogRepository chatLogRepository;
  private final ReportRepository reportRepository;
  private final ChatMessageUtil chatMessageUtil;
  private final ClovaApiConverter clovaApiConverter;
  private final JsonConverter jsonConverter;
  private final ChatLogConverter chatLogConverter;

  @Override
  @Transactional
  public ChatResponseDTO sendMessage(Long reportId, ChatRequestDTO request) {
    try {
      log.info("채팅 메시지 요청 - reportId: {}, userInput: {}", reportId, request.getUserInput());

      // 1. 리포트 조회
      Report report = reportRepository.findById(reportId)
          .orElseThrow(() -> new ChatHandler(ChatErrorStatus.CHAT_REPORT_NOT_FOUND));

      // 2. 사용자 메시지를 ChatLog에 저장
      ChatLog userChatLog = ChatLog.builder()
          .senderType(SenderType.USER)
          .message(request.getUserInput())
          .reportId(report.getReportId())
          .build();
      chatLogRepository.save(userChatLog);

      // 3. Clova API 호출을 위한 요청 생성
      var clovaRequest = clovaApiConverter.createChatRequest(report.getChatbotContext(), report.getArticleContent(),
          request.getUserInput());

      // 4. Clova API 호출
      var clovaResponse = clovaApiConverter.callChatAPI(clovaRequest);

      // 5. AI 응답 메시지 다듬기
      String polishedResponse = chatMessageUtil.polishMessage(clovaResponse.getResult().getMessage().getContent());

      // 6. AI 응답을 ChatLog에 저장
      ChatLog aiChatLog = ChatLog.builder()
          .senderType(SenderType.AI)
          .message(polishedResponse)
          .reportId(report.getReportId())
          .build();
      chatLogRepository.save(aiChatLog);

      log.info("채팅 메시지 처리 완료 - reportId: {}, AI 응답 길이: {}",
          reportId, polishedResponse.length());

      return ChatResponseDTO.builder()
          .aiResponse(polishedResponse)
          .build();

    } catch (Exception e) {
      log.error("채팅 메시지 처리 실패 - reportId: {}, 에러: {}", reportId, e.getMessage(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ChatLogListResponseDTO getChatLogs(Long reportId) {
    try {
      log.info("채팅 로그 조회 요청 - reportId: {}", reportId);

      // 1. 리포트 존재 여부 확인
      Report report = reportRepository.findById(reportId)
          .orElseThrow(() -> new ChatHandler(ChatErrorStatus.CHAT_REPORT_NOT_FOUND));

      // 2. 채팅 로그 조회 (시간 순으로 오름차순)
      List<ChatLog> chatLogs = chatLogRepository.findByReportIdOrderByCreatedAtAsc(reportId);

      log.info("채팅 로그 조회 완료 - reportId: {}, 로그 개수: {}", reportId, chatLogs.size());

      return chatLogConverter.toResponseDto(reportId, chatLogs);

    } catch (Exception e) {
      log.error("채팅 로그 조회 실패 - reportId: {}, 에러: {}", reportId, e.getMessage(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);
    }
  }

  @Override
  @Transactional
  public RecommendQuestionsResponseDTO getRecommendQuestions(Long reportId) {
    try {
      log.info("추천 질문 요청 - reportId: {}", reportId);

      // 1. 리포트 조회
      Report report = reportRepository.findById(reportId)
          .orElseThrow(() -> new ChatHandler(ChatErrorStatus.CHAT_REPORT_NOT_FOUND));

      // 2. 추천 질문 요청 생성
      var recommendRequest = clovaApiConverter.createRecommendRequest(report.getChatbotContext());

      // 3. Clova API 호출
      var recommendResponse = clovaApiConverter.callRecommendAPI(recommendRequest);

      // 4. 응답에서 질문 목록 추출
      List<String> questions = jsonConverter.parseJsonArray(recommendResponse.getResult().getMessage().getContent());

      log.info("추천 질문 생성 완료 - reportId: {}, 질문 개수: {}", reportId, questions.size());

      return RecommendQuestionsResponseDTO.builder()
          .questions(questions)
          .build();

    } catch (Exception e) {
      log.error("추천 질문 생성 실패 - reportId: {}, 에러: {}", reportId, e.getMessage(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);
    }
  }
}