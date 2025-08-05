package com.perfact.be.domain.user.controller;

import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.exception.status.UserSuccessStatus;
import com.perfact.be.domain.user.service.UserService;
import com.perfact.be.global.apiPayload.ApiResponse;
import com.perfact.be.global.resolver.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="User", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @Operation(summary = "구독 상태 확인", description = "현재 사용자의 구독 상태를 확인합니다.")
  @GetMapping("/subscribe")
  public ApiResponse<Object> getSubscribeStatus(
      @Parameter(hidden = true) @CurrentUser User loginUser
  ) {
    //TODO : 구독상태 확인 서비스 로직 구현 예정
    return ApiResponse.onSuccess(UserSuccessStatus.GET_SUBSCRIBE_STATUS_SUCCESS);
  }

  @Operation(summary = "구독하기", description = "사용자가 구독을 신청합니다.")
  @PostMapping("/subscribe")
  public ApiResponse<Object> subscribe(
      @Parameter(hidden = true) @CurrentUser User loginUser
  ) {
    //TODO : 구독하기 서비스 로직 구현 예정
    return ApiResponse.onSuccess(UserSuccessStatus.SUBSCRIBE_SUCCESS);
  }

  @Operation(summary = "구독 해지하기", description = "사용자가 구독을 해지합니다.")
  @PatchMapping("/subscribe")
  public ApiResponse<Object> unsubscribe(
      @Parameter(hidden = true) @CurrentUser User loginUser
  ) {
    //TODO : 구독 해지하기 서비스 로직 구현 예정
    return ApiResponse.onSuccess(UserSuccessStatus.UNSUBSCRIBE_SUCCESS);
  }


}
