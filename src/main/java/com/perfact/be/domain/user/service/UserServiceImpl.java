package com.perfact.be.domain.user.service;

import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.credit.entity.SubscriptionPlans;
import com.perfact.be.domain.credit.entity.enums.CreditLogType;
import com.perfact.be.domain.credit.entity.enums.PlanType;
import com.perfact.be.domain.credit.exception.CreditHandler;
import com.perfact.be.domain.credit.exception.status.CreditErrorStatus;
import com.perfact.be.domain.credit.repository.CreditLogRepository;
import com.perfact.be.domain.credit.repository.SubscriptionPlansRepository;
import com.perfact.be.domain.user.dto.UserResponseDto.SubscribeStatusResponse;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.Role;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.entity.enums.UserStatus;
import com.perfact.be.domain.user.exception.UserHandler;
import com.perfact.be.domain.user.exception.status.UserErrorStatus;
import com.perfact.be.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final SubscriptionPlansRepository subscriptionPlansRepository;
  private final CreditLogRepository creditLogRepository;

  @Override
  @Transactional
  public User findOrCreateUser(NaverUserProfile profile) {
    try {
      return userRepository.findBySocialIdAndSocialType(profile.getId(), SocialType.NAVER)
          .orElseGet(() -> registerNewUser(profile));
    } catch (Exception e) {
      throw new UserHandler(UserErrorStatus.USER_LOOKUP_FAILED);
    }
  }

  @Override
  @Transactional
  public User findById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserHandler(UserErrorStatus.USER_NOT_FOUND));
  }

  @Override
  public SubscribeStatusResponse getSubscribeStatus(User loginUser) {
    SubscriptionPlans plan = loginUser.getPlan();

    String planName = plan != null ? plan.getName().toString() : "UNKNOWN";
    boolean isFreePlan = "FREE".equals(planName);
    String subscribeStatus = isFreePlan ? "무료 플랜 사용 중" : "유료 플랜 사용 중";
    String nextBillingDate = isFreePlan ? "무료 플랜 사용 중" : "미정";
    Long dailyCredit = plan != null ? plan.getDailyCredit() : 0L;

    LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
    LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

    YearMonth now = YearMonth.now();
    LocalDateTime startOfMonth = now.atDay(1).atStartOfDay();
    LocalDateTime startOfNextMonth = now.plusMonths(1).atDay(1).atStartOfDay();

    // 오늘 사용량
    Long todayUsage = Optional.ofNullable(
        creditLogRepository.sumUsedCreditByUserAndTypeAndCreatedAtBetween(
            loginUser,
            CreditLogType.REPORT_CREATE,
            startOfToday,
            startOfTomorrow
        )
    ).map(Math::abs).orElse(0L);

    // 이번 달 사용량
    Long thisMonthUsage = Optional.ofNullable(
        creditLogRepository.sumUsedCreditByUserAndTypeAndCreatedAtBetween(
            loginUser,
            CreditLogType.REPORT_CREATE,
            startOfMonth,
            startOfNextMonth
        )
    ).map(Math::abs).orElse(0L);
    return new SubscribeStatusResponse(
        planName,
        subscribeStatus,
        nextBillingDate,
        dailyCredit,
        todayUsage,
        thisMonthUsage
    );
  }


  @Override
  @Transactional
  public void decreaseCredit(User user, int amount) {
    long newCredit = user.getCredit() - amount;
    if (newCredit < 0) {
      throw new CreditHandler(CreditErrorStatus.CREDIT_NOT_ENOUGH);
    }
    user.setCredit(newCredit);
    userRepository.save(user);
  }


  private User registerNewUser(NaverUserProfile profile) {
    SubscriptionPlans defaultPlan = subscriptionPlansRepository.findByName(PlanType.FREE)
        .orElseThrow(() -> new UserHandler(UserErrorStatus.PLAN_NOT_FOUND));
    try {
      User newUser = User.builder()
          .email(profile.getEmail())
          .nickname(profile.getNickname())
          .socialId(profile.getId())
          .socialType(SocialType.NAVER)
          .role(Role.ROLE_USER)
          .status(UserStatus.ACTIVE)
          .plan(defaultPlan)
          .isSubscribe(false)
          .isNotificationAgreed(false)
          .build();

      return userRepository.save(newUser);
    } catch (Exception e) {
      throw new UserHandler(UserErrorStatus.USER_CREATION_FAILED);
    }
  }
}
