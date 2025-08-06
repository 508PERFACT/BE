package com.perfact.be.domain.report.repository;

import com.perfact.be.domain.report.entity.ReportBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportBadgeRepository extends JpaRepository<ReportBadge, Long> {
  List<ReportBadge> findByReportId(Long reportId);
}