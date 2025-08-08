package com.perfact.be.domain.chat.controller;

import com.perfact.be.domain.chat.dto.ChatLogListResponseDTO;
import com.perfact.be.domain.chat.dto.ChatRequestDTO;
import com.perfact.be.domain.chat.dto.ChatResponseDTO;
import com.perfact.be.domain.chat.dto.RecommendQuestionsResponseDTO;
import com.perfact.be.domain.chat.service.ChatService;
import com.perfact.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "챗봇 API")
public class ChatController {

  private final ChatService chatService;

  @Operation(summary = "리포트 기반 채팅", description = "특정 리포트의 분석 결과를 바탕으로 AI와 채팅합니다.")
  @PostMapping("/{reportId}/chat")
  public ApiResponse<ChatResponseDTO> sendMessage(
      @Parameter(description = "리포트 ID", required = true, example = "1") @PathVariable Long reportId,
      @Parameter(description = "채팅 요청", required = true) @RequestBody ChatRequestDTO request) {

    log.info("채팅 요청 - reportId: {}, userInput: {}", reportId, request.getUserInput());

    ChatResponseDTO response = chatService.sendMessage(reportId, request);

    return ApiResponse.onSuccess(response);
  }

  @Operation(summary = "채팅 로그 조회", description = "특정 리포트의 채팅 로그를 시간 순으로 조회합니다.")
  @GetMapping("/{reportId}/chat")
  public ApiResponse<ChatLogListResponseDTO> getChatLogs(
      @Parameter(description = "리포트 ID", required = true, example = "1") @PathVariable Long reportId) {

    log.info("채팅 로그 조회 요청 - reportId: {}", reportId);

    ChatLogListResponseDTO response = chatService.getChatLogs(reportId);

    return ApiResponse.onSuccess(response);
  }

  @Operation(summary = "추천 질문 조회", description = "특정 리포트의 분석 결과를 바탕으로 AI가 추천하는 질문을 조회합니다.")
  @GetMapping("/{reportId}/chat/recommend")
  public ApiResponse<RecommendQuestionsResponseDTO> getRecommendQuestions(
      @Parameter(description = "리포트 ID", required = true, example = "1") @PathVariable Long reportId) {

    log.info("추천 질문 조회 요청 - reportId: {}", reportId);

    RecommendQuestionsResponseDTO response = chatService.getRecommendQuestions(reportId);

    return ApiResponse.onSuccess(response);
  }
}