package com.perfact.be.domain.alt.entity;

import com.perfact.be.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "perspective_comparisons")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerspectiveComparison extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "perspective_comparison_id")
  private Long perspectiveComparisonId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "alternative_article_id")
  private AlternativeArticle alternativeArticle;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "article", columnDefinition = "TEXT")
  private String article;

  @Column(name = "alt_article", columnDefinition = "TEXT")
  private String altArticle;
}