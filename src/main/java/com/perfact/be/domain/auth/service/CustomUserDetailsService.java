package com.perfact.be.domain.auth.service;


import com.perfact.be.domain.user.entity.enums.UserStatus;
import com.perfact.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    return userRepository.findById(Long.valueOf(userId))
        .filter(user -> user.getStatus() == UserStatus.ACTIVE)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않거나 비활성화된 유저입니다."));
  }

}

