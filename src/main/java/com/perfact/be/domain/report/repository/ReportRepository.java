package com.perfact.be.domain.report.repository;

import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  List<Report> findByUserOrderByCreatedAtDesc(User user);

  List<Report> findByUserAndCategoryOrderByCreatedAtDesc(User user, String category);

  Page<Report> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}