package com.perfact.be.global.apiPayload.code.status;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
  _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
  _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
  _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
  _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


  // 인증 관련
  MALFORMED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4101", "잘못 구성된 JWT 형식입니다."),
  UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4102", "지원하지 않는 JWT 형식입니다."),
  EMPTY_JWT_CLAIMS(HttpStatus.BAD_REQUEST, "AUTH4103", "JWT 클레임이 비어 있습니다."),
  INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH4111", "유효하지 않은 JWT 서명입니다."),
  EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4112", "JWT 토큰이 만료되었습니다."),
  INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "AUTH4114", "유효하지 않은 토큰 타입입니다."),
  MISSING_JWT(HttpStatus.UNAUTHORIZED, "AUTH4115", "JWT 토큰이 없습니다."),

  // 사용자 관련
  USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH4111", "존재하지 않는 사용자입니다."),
  INACTIVE_USER(HttpStatus.FORBIDDEN, "AUTH4131", "비활성화된 사용자입니다."),


  // 회원 관련
  INVALID_USER(HttpStatus.BAD_REQUEST, "USER4001", "존재하지 않는 유저입니다."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ErrorReasonDto getReason() {
    return ErrorReasonDto.builder()
        .isSuccess(true)
        .message(message)
        .code(code)
        .build();
  }

  @Override
  public ErrorReasonDto getReasonHttpStatus() {
    return ErrorReasonDto.builder()
        .httpStatus(httpStatus)
        .isSuccess(true)
        .code(code)
        .message(message)
        .build();
  }

}
