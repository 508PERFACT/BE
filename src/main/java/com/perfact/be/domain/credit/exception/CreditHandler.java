package com.perfact.be.domain.credit.exception;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class CreditHandler extends GeneralException {
  public CreditHandler(BaseErrorCode code) {
    super(code);
  }
}