package com.e205.domain.member.entity;

import com.e205.common.audit.BaseTime;
import com.e205.log.LoggableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member extends BaseTime implements LoggableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private Integer bagId;

  @Column(nullable = false, length = 15)
  private String nickname;

  @Column(nullable = false, length = 20)
  private String password;

  @Column(nullable = false, length = 13)
  private String phone;

  @Column(nullable = false, length = 30)
  private String email;
}
