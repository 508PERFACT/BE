package com.perfact.be.domain.user.service;

import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.exception.UserHandler;
import com.perfact.be.domain.user.exception.status.UserErrorStatus;
import com.perfact.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User findOrCreateUser(NaverUserProfile profile) {
    try {
      return userRepository.findBySocialIdAndSocialType(profile.getId(), SocialType.NAVER)
          .orElseGet(() -> registerNewUser(profile));
    } catch (Exception e) {
      throw new UserHandler(UserErrorStatus.USER_LOOKUP_FAILED);
    }
  }

  private User registerNewUser(NaverUserProfile profile) {
    try {
      // 사용자 구독 정보 등 초기 세팅 필요
      User newUser = User.builder()
          .email(profile.getEmail())
          .nickname(profile.getNickname())
          .socialId(profile.getId())
          .socialType(SocialType.NAVER)
          .role("USER")
          .build();

      return userRepository.save(newUser);
    } catch (Exception e) {
      throw new UserHandler(UserErrorStatus.USER_CREATION_FAILED);
    }
  }
}
