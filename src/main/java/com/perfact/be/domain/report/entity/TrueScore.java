package com.perfact.be.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "true_scores")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrueScore {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "true_score_id")
  private Long trueScoreId;

  @Column(name = "report_id")
  private Long reportId;

  @Column(name = "source_reliability")
  private Integer sourceReliability;

  @Column(name = "factual_basis")
  private Integer factualBasis;

  @Column(name = "ad_exaggeration")
  private Integer adExaggeration;

  @Column(name = "bias")
  private Integer bias;

  @Column(name = "article_structure")
  private Integer articleStructure;

  @Column(name = "overall_score")
  private Integer overallScore;

  public void setReportId(Long reportId) {
    this.reportId = reportId;
  }
}