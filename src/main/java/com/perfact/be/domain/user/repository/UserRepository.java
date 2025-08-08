package com.perfact.be.domain.user.repository;

import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.entity.enums.UserStatus;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);


  @EntityGraph(attributePaths = "plan")
  @Query("SELECT u FROM User u WHERE u.status = :status")
  List<User> findByStatusWithPlan(@Param("status")UserStatus status);
}