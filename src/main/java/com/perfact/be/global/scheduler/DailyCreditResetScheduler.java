package com.perfact.be.global.scheduler;

import com.perfact.be.domain.credit.entity.CreditLog;
import com.perfact.be.domain.credit.entity.SubscriptionPlans;
import com.perfact.be.domain.credit.entity.enums.CreditLogType;
import com.perfact.be.domain.credit.repository.CreditLogRepository;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.UserStatus;
import com.perfact.be.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyCreditResetScheduler {

  private final UserRepository userRepository;
  private final CreditLogRepository creditLogRepository;

  @Scheduled(cron = "0 0 0 * * *")
//  @Scheduled(cron = "*/30 * * * * *")
  @Transactional
  public void resetsCredits() {
    log.info("[크레딧 스케줄러] : 자정 크레딧 초기화 로직 수행");

    List<User> users = userRepository.findByStatusWithPlan(UserStatus.ACTIVE);
    List<CreditLog> logs = new ArrayList<>();

    for (User user : users) {
      SubscriptionPlans plans = user.getPlan();
      Long dailyCredit = plans.getDailyCredit();

      if(dailyCredit == null || dailyCredit == -1){
        continue;
      }

      Long before = user.getCredit();
      user.setCredit(dailyCredit);

      logs.add(CreditLog.builder()
              .user(user)
              .amount(dailyCredit-before)
              .balance(dailyCredit)
              .type(CreditLogType.DAILY_RESET)
              .description(CreditLogType.DAILY_RESET.getDescription())
          .build());

    }
    creditLogRepository.saveAll(logs);
    log.info("[크레딧 스케줄러] : 자정 크레딧 초기화 로직 수행 완료. 총 {}명", logs.size());
  }
}
