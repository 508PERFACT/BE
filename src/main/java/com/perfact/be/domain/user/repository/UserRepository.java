package com.perfact.be.domain.user.repository;

import com.perfact.be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findBySocialIdAndSocialType(String socialId, String socialType);

  Optional<User> findByEmail(String email);
}