package com.perfact.be.domain.user.exception;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class UserHandler extends GeneralException {
  public UserHandler(BaseErrorCode code) {
    super(code);
  }

}
