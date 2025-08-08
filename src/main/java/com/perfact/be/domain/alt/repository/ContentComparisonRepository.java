package com.perfact.be.domain.alt.repository;

import com.perfact.be.domain.alt.entity.ContentComparison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentComparisonRepository extends JpaRepository<ContentComparison, Long> {
  List<ContentComparison> findByAlternativeArticleAlternativeArticleId(Long alternativeArticleId);
}
