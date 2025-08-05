package com.perfact.be.domain.user.repository;

import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);


}