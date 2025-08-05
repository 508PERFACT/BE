package com.perfact.be.domain.user.entity;

import com.perfact.be.domain.user.entity.enums.Role;
import com.perfact.be.domain.user.entity.enums.SocialType;
import com.perfact.be.domain.user.entity.enums.UserStatus;
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
  @Column(name = "email", length = 255)
  private String email;
  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private Role role;
  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private UserStatus status = UserStatus.ACTIVE;



  @Column(name = "is_subscribe", columnDefinition = "boolean default false")
  private Boolean isSubscribe = false;

  @Column(columnDefinition = "boolean default false")
  private Boolean isNotificationAgreed = false;

  @Column(columnDefinition = "bigint default 3")
  private Long credit = 3L;


}