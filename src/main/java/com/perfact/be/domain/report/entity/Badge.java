package com.perfact.be.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badges")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "badge_id")
  private Long badgeId;

  @Enumerated(EnumType.STRING)
  @Column(name = "badge_name")
  private BadgeName badgeName;

  public enum BadgeName {
    RELIABLE_SOURCE("공신력 있는 출처"),
    BALANCED_ARTICLE("균형 잡힌 기사"),
    EXCELLENT_WARNING("주의 환기 우수"),
    PARTIALLY_TRUSTWORTHY("부분적인 신뢰 가능"),
    NO_EXPERT_CITATION("전문가 인용 없음"),
    ADVERTORIAL_ARTICLE("광고성 기사"),
    FACT_VERIFICATION_IMPOSSIBLE("사실 검증 불가"),
    UNTRUSTWORTHY("신뢰 불가"),
    ADVERTISING_PURPOSE("광고 목적"),
    MANY_EXAGGERATED_EXPRESSIONS("과장 표현 다수");

    private final String displayName;

    BadgeName(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }
}