package com.perfact.be.global.aop;

import com.perfact.be.domain.credit.entity.enums.CreditLogType;
import com.perfact.be.domain.credit.exception.CreditHandler;
import com.perfact.be.domain.credit.exception.status.CreditErrorStatus;
import com.perfact.be.domain.credit.service.CreditLogService;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.exception.UserHandler;
import com.perfact.be.domain.user.exception.status.UserErrorStatus;
import com.perfact.be.domain.user.service.UserService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CreditAspect {

  private final UserService userService;
  private final CreditLogService creditLogService;

  @Pointcut("@annotation(creditCheck)")
  public void creditCheckPointcut(CreditCheck creditCheck) {}

  @Around("creditCheckPointcut(creditCheck)")
  public Object around(ProceedingJoinPoint joinPoint, CreditCheck creditCheck) throws Throwable {
    User user = extractUser(joinPoint.getArgs());
    int cost = creditCheck.cost();

    if (user.getCredit() < cost) {
      throw new CreditHandler(CreditErrorStatus.CREDIT_NOT_ENOUGH);
    }

    Object result = joinPoint.proceed();

    userService.decreaseCredit(user, cost);
    creditLogService.createLog(user, -cost, user.getCredit(), CreditLogType.REPORT_CREATE, CreditLogType.REPORT_CREATE.getDescription());

    return result;
  }

  private User extractUser(Object[] args) {
    return Arrays.stream(args)
        .filter(arg -> arg instanceof User)
        .map(arg -> (User) arg)
        .findFirst()
        .orElseThrow(() -> new UserHandler(UserErrorStatus.USER_LOOKUP_FAILED));
  }
}


