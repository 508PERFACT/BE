package com.perfact.be.global.exception.handler;


import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class GlobalHandler extends GeneralException {

  public GlobalHandler(BaseErrorCode code) {
    super(code);
  }
}
