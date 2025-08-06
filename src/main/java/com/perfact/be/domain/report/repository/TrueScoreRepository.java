package com.perfact.be.domain.report.repository;

import com.perfact.be.domain.report.entity.TrueScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrueScoreRepository extends JpaRepository<TrueScore, Long> {
  Optional<TrueScore> findByReportId(Long reportId);
}