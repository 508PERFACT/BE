package com.perfact.be.domain.chat.repository;

import com.perfact.be.domain.chat.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
  List<ChatLog> findByReportIdOrderByCreatedAtAsc(Long reportId);
}