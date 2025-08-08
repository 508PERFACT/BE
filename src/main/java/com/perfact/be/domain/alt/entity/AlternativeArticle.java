package com.perfact.be.domain.alt.entity;

import com.perfact.be.domain.report.entity.Report;
import com.perfact.be.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "alternative_articles")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlternativeArticle extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "alternative_article_id")
  private Long alternativeArticleId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id")
  private Report report;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "url", columnDefinition = "TEXT")
  private String url;

  @Column(name = "publication_date")
  private LocalDate publicationDate;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Column(name = "result", columnDefinition = "TEXT")
  private String result;
}
