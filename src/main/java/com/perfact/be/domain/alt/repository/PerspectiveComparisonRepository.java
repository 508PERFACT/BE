package com.perfact.be.domain.alt.repository;

import com.perfact.be.domain.alt.entity.PerspectiveComparison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerspectiveComparisonRepository extends JpaRepository<PerspectiveComparison, Long> {
  List<PerspectiveComparison> findByAlternativeArticleAlternativeArticleId(Long alternativeArticleId);
}
