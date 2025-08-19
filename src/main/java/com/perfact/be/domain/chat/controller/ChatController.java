package com.perfact.be.domain.chat.controller;


import com.perfact.be.domain.chat.dto.ChatRequestDTO;
import com.perfact.be.domain.chat.dto.ChatResponse;
import com.perfact.be.domain.chat.service.ChatService;
import com.perfact.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "챗봇 API")
public class ChatController {

  private final ChatService chatServiceImpl;

  @Operation(summary = "리포트 기반 채팅", description = "특정 리포트의 분석 결과를 바탕으로 AI와 채팅합니다.")
  @PostMapping("/{reportId}/chat")
  public ApiResponse<ChatResponse.ChatResponseDTO> sendMessage(
      @Parameter(description = "리포트 ID", required = true, example = "1") @PathVariable Long reportId,
      @Parameter(description = "채팅 요청", required = true) @RequestBody ChatRequestDTO request) {
    ChatResponse.ChatResponseDTO response = chatServiceImpl.sendMessage(reportId, request);
    return ApiResponse.onSuccess(response);
  }

  @Operation(summary = "채팅 로그 조회", description = "특정 리포트의 채팅 로그를 시간 순으로 조회합니다.")
  @GetMapping("/{reportId}/chat")
  public ApiResponse<ChatResponse.ChatLogListResponseDTO> getChatLogs(
      @Parameter(description = "리포트 ID", required = true, example = "1") @PathVariable Long reportId) {
    ChatResponse.ChatLogListResponseDTO response = chatServiceImpl.getChatLogs(reportId);
    return ApiResponse.onSuccess(response);
  }

  @Operation(summary = "추천 질문 조회", description = "특정 리포트의 분석 결과를 바탕으로 AI가 추천하는 질문을 조회합니다.")
  @GetMapping("/{reportId}/chat/recommend")
  public ApiResponse<ChatResponse.RecommendQuestionsResponseDTO> getRecommendQuestions(
      @Parameter(description = "리포트 ID", required = true, example = "1") @PathVariable Long reportId) {
    ChatResponse.RecommendQuestionsResponseDTO response = chatServiceImpl.getRecommendQuestions(reportId);
    return ApiResponse.onSuccess(response);
  }

}