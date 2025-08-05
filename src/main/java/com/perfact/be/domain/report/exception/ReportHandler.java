package com.perfact.be.domain.report.exception;

import com.perfact.be.global.apiPayload.code.BaseErrorCode;
import com.perfact.be.global.exception.GeneralException;

public class ReportHandler extends GeneralException {
  public ReportHandler(BaseErrorCode code) {
    super(code);
  }
}