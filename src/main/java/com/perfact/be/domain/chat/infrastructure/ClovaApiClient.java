package com.perfact.be.domain.chat.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfact.be.domain.chat.dto.ClovaChatRequestDTO;
import com.perfact.be.domain.chat.dto.ClovaChatResponseDTO;
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
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaApiClient {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${api.clova.chat-url}")
  private String CLOVA_CHAT_URL;

  @Value("${api.clova.api-key}")
  private String CLOVA_API_KEY;


  // Clova 채팅 API를 호출
  public ClovaChatResponseDTO callChatAPI(ClovaChatRequestDTO request) {
    return post(CLOVA_CHAT_URL, request, ClovaChatResponseDTO.class, "Clova 채팅");
  }

  // Clova 추천 질문 API를 호출
  public ClovaChatResponseDTO callRecommendAPI(ClovaChatRequestDTO request) {
    return post(CLOVA_CHAT_URL, request, ClovaChatResponseDTO.class, "Clova 추천");
  }

  private <TReq, TRes> TRes post(String url, TReq body, Class<TRes> resType, String logName) {
    try {

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(CLOVA_API_KEY);

      String json = serialize(body);
      HttpEntity<String> entity = new HttpEntity<>(json, headers);

      log.info("{} API 호출 → {}", logName, url);
      ResponseEntity<TRes> response = restTemplate.exchange(url, HttpMethod.POST, entity, resType);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        log.debug("{} API 응답 수신 (status: {})", logName, response.getStatusCode());
        return response.getBody();
      }
      log.error("{} API 비정상 응답 (status: {}, body=null)", logName, response.getStatusCode());
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);

    }catch (RestClientResponseException e) {
      log.error("{} API 실패 (status:{}, body:{})", logName, e.getRawStatusCode(), e.getResponseBodyAsString(), e);
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);
    } catch (Exception e) {
      log.error("{} API 호출 중 예외", logName, e);
      throw new ChatHandler(ChatErrorStatus.CHAT_API_CALL_FAILED);
    }

  }

  private String serialize(Object body) throws JsonProcessingException {
    return objectMapper.writeValueAsString(body);
  }


}
