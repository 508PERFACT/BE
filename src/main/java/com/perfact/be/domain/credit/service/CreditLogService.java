package com.perfact.be.domain.credit.service;

import com.perfact.be.domain.credit.entity.enums.CreditLogType;
import com.perfact.be.domain.user.entity.User;

public interface CreditLogService {

  void createLog(User user, long amount, long balance, CreditLogType type, String description);
}
