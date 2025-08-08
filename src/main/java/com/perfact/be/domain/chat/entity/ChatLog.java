package com.perfact.be.domain.chat.entity;

import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatLog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_id")
  private Long chatId;

  @Enumerated(EnumType.STRING)
  @Column(name = "sender_type")
  private SenderType senderType;

  @Column(name = "message", columnDefinition = "TEXT")
  private String message;

  @Column(name = "report_id")
  private Long reportId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id", insertable = false, updatable = false)
  private Report report;
}
