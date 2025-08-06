package com.perfact.be.domain.auth.service;

import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.Role;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.entity.enums.UserStatus;
import com.perfact.be.domain.user.repository.UserRepository;
import com.perfact.be.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class DevAuthService {

  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;

  @Transactional
  public String createFakeUserAndGetToken() {
    String fakeSocialId = "dev-" + UUID.randomUUID();

    User fakeUser = User.builder()
        .email(fakeSocialId + "@dev.local")
        .nickname("개발용유저")
        .socialId(fakeSocialId)
        .socialType(SocialType.NAVER)
        .role(Role.ROLE_USER)
        .status(UserStatus.ACTIVE)
        .build();

    userRepository.save(fakeUser);
    return jwtProvider.generateAccessToken(fakeUser.getId(), fakeUser.getSocialId());
  }
}

