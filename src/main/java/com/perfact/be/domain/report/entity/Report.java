package com.perfact.be.domain.report.entity;

import com.perfact.be.domain.user.entity.User;
import com.perfact.be.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long reportId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "category", length = 255)
  private String category;

  @Column(name = "url", length = 1000)
  private String url;

  @Column(name = "publisher", length = 50)
  private String publisher;

  @Column(name = "publication_date")
  private LocalDate publicationDate;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Builder
  public Report(User user, String title, String category, String url,
      String publisher, LocalDate publicationDate, String summary) {
    this.user = user;
    this.title = title;
    this.category = category;
    this.url = url;
    this.publisher = publisher;
    this.publicationDate = publicationDate;
    this.summary = summary;
  }
}