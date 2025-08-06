package com.perfact.be.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverTokenResponse {
  private String access_token;
  private String refresh_token;
  private String token_type;
  private String expires_in;
}
