package com.perfact.be.domain.user.entity;

import com.perfact.be.domain.user.entity.enums.SocialType;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  private String nickname;

  @Enumerated(EnumType.STRING)
  private SocialType socialType;

  private String socialId;

  private String role;
}