package com.perfact.be.domain.chat.exception;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class ChatHandler extends GeneralException {
  public ChatHandler(BaseErrorCode code) {
    super(code);
  }
}