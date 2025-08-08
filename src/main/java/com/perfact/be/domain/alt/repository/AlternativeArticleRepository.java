package com.perfact.be.domain.alt.repository;

import com.perfact.be.domain.alt.entity.AlternativeArticle;
import com.perfact.be.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlternativeArticleRepository extends JpaRepository<AlternativeArticle, Long> {
  Optional<AlternativeArticle> findByReport(Report report);

  Optional<AlternativeArticle> findByReportReportId(Long reportId);
}
