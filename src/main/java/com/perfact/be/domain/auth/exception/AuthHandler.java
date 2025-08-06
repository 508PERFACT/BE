package com.perfact.be.domain.auth.exception;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class AuthHandler extends GeneralException {
  public AuthHandler(BaseErrorCode code) {
    super(code);
  }

}
