package com.perfact.be.domain.user.service;

import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.exception.UserHandler;
import com.perfact.be.domain.user.exception.status.UserErrorStatus;
import com.perfact.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User findOrCreateUser(NaverUserProfile profile) {
    return userRepository.findBySocialIdAndSocialType(profile.getId(), SocialType.NAVER)
        .orElseGet(() -> registerNewUser(profile));
  }

  private User registerNewUser(NaverUserProfile profile) {
    try {
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
