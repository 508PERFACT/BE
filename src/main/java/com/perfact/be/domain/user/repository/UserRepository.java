package com.perfact.be.domain.user.repository;

import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.SocialType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);


}
