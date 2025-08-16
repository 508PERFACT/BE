package com.perfact.be.domain.user.entity;

import com.perfact.be.domain.credit.entity.CreditLog;
import com.perfact.be.domain.credit.entity.SubscriptionPlans;
import com.perfact.be.domain.user.entity.enums.Role;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.entity.enums.UserStatus;
import com.perfact.be.global.common.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;
  @Column(name = "social_id", length = 255)
  private String socialId;
  @Enumerated(EnumType.STRING)
  private SocialType socialType;
  @Column(name = "nickname", length = 20)
  private String nickname;
  @Column(name = "email", length = 255)
  private String email;
  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private Role role;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(length = 20, nullable = false)
  private UserStatus status = UserStatus.ACTIVE;



  @Builder.Default
  @Column(name = "is_subscribe", columnDefinition = "boolean default false")
  private Boolean isSubscribe = false;

  @Builder.Default
  @Column(columnDefinition = "boolean default false")
  private Boolean isNotificationAgreed = false;

  @Builder.Default
  @Column(columnDefinition = "bigint default 3")
  private Long credit = 3L;

  @Builder.Default
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CreditLog> creditLogs = new ArrayList<>();


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id")
  private SubscriptionPlans plan;

  public void setCredit(Long dailyCredit) {
    this.credit = dailyCredit;
  }
}