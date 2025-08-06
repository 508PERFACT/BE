package com.perfact.be.domain.report.repository;

import com.perfact.be.domain.report.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
  Optional<Badge> findByBadgeName(Badge.BadgeName badgeName);
}