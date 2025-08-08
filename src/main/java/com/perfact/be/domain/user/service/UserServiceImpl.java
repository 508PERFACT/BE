package com.perfact.be.domain.user.service;

import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.credit.entity.SubscriptionPlans;
import com.perfact.be.domain.credit.entity.enums.PlanType;
import com.perfact.be.domain.credit.repository.SubscriptionPlansRepository;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.Role;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.entity.enums.UserStatus;
import com.perfact.be.domain.user.exception.UserHandler;
import com.perfact.be.domain.user.exception.status.UserErrorStatus;
import com.perfact.be.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final SubscriptionPlansRepository subscriptionPlansRepository;

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
