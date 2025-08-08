package com.perfact.be.domain.alt.exception;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class AltHandler extends GeneralException {
  public AltHandler(BaseErrorCode code) {
    super(code);
  }
}
