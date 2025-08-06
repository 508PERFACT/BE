package com.perfact.be.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserInfoResponse {
  private String resultcode;
  private String message;
  private NaverUserProfile response;
}
