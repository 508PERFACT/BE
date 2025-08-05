package com.perfact.be.domain.user.entity;

import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.global.common.BaseEntity;
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

  @Column(name = "nickname", length = 10)
  private String nickname;

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

  private String role;

}