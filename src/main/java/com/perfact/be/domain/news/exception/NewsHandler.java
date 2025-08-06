package com.perfact.be.domain.news.exception;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class NewsHandler extends GeneralException {
  public NewsHandler(BaseErrorCode code) {
    super(code);
  }
}