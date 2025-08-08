package com.perfact.be.domain.credit.service;

import com.perfact.be.domain.credit.entity.CreditLog;
import com.perfact.be.domain.credit.entity.enums.CreditLogType;
import com.perfact.be.domain.credit.repository.CreditLogRepository;
import com.perfact.be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreditLogServiceImpl implements CreditLogService{
  private final CreditLogRepository creditLogRepository;

  @Override
  public void createLog(User user, long amount, long balance, CreditLogType type, String description) {
    CreditLog log = CreditLog.builder()
        .user(user)
        .amount(amount)
        .balance(balance)
        .type(type)
        .description(description)
        .build();
    creditLogRepository.save(log);
  }
}
