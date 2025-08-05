package com.perfact.be.domain.auth.service;

import com.perfact.be.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String socialId;
  private final String role;

  public CustomUserDetails(User user) {
    this.userId = user.getId();
    this.socialId = user.getSocialId();
    this.role = user.getRole().name();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> role);
  }

  @Override
  public String getPassword() {
    return ""; // 네이버 로그인이라 비번 안씀
  }

  @Override
  public String getUsername() {
    return socialId;
  }

  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }
}

