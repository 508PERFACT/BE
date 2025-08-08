package com.perfact.be.domain.report.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long reportId;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "category", length = 255)
  private String category;

  @Column(name = "one_line_summary", columnDefinition = "TEXT")
  private String oneLineSummary;

  @Column(name = "url", length = 1000)
  private String url;

  @Column(name = "publisher", length = 50)
  private String publisher;

  @Column(name = "publication_date")
  private LocalDate publicationDate;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Column(name = "chatbot_context", columnDefinition = "TEXT")
  private String chatbotContext;

  public void setUser(User user) {
    this.user = user;
  }
}