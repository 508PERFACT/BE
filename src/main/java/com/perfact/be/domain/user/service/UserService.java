package com.perfact.be.domain.user.service;

import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.user.entity.User;

public interface UserService {

  User findOrCreateUser(NaverUserProfile profile);
}
