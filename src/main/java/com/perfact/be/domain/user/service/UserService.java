package com.perfact.be.domain.user.service;

import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.user.dto.UserResponseDto.NicknameResponse;
import com.perfact.be.domain.user.dto.UserResponseDto.SubscribeStatusResponse;
import com.perfact.be.domain.user.entity.User;

public interface UserService {

  User findOrCreateUser(NaverUserProfile profile);

  Object findById(Long userId);

  SubscribeStatusResponse getSubscribeStatus(User loginUser);

  void decreaseCredit(User user, int cost);

  NicknameResponse getNickname(User loginUser);

  User createGuestUser(String deviceUuid);
}
