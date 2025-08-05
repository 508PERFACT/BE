package com.perfact.be.domain.user.entity;

import com.perfact.be.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "social_id", length = 255)
  private String socialId;

  @Column(name = "social_type", length = 50)
  private String socialType;

  @Column(name = "name", length = 10)
  private String name;

  @Column(name = "birth")
  private Integer birth;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "is_subscribe", length = 255)
  private String isSubscribe;

  @Column(name = "is_notification_agreed")
  private Boolean isNotificationAgreed;

  @Column(name = "credit")
  private Long credit;

  @Builder
  public User(String socialId, String socialType, String name, Integer birth,
      String email, String isSubscribe, Boolean isNotificationAgreed, Long credit) {
    this.socialId = socialId;
    this.socialType = socialType;
    this.name = name;
    this.birth = birth;
    this.email = email;
    this.isSubscribe = isSubscribe;
    this.isNotificationAgreed = isNotificationAgreed;
    this.credit = credit;
  }
}