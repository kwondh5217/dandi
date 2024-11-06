package com.e205.domain.member.entity;

import com.e205.command.bag.payload.EmailStatus;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.payload.MemberAuthPayload;
import com.e205.common.audit.BaseTime;
import com.e205.log.LoggableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseTime implements LoggableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private Integer bagId;

  @Column(nullable = false, length = 15)
  private String nickname;

  @Column(nullable = false, length = 70)
  private String password;

  @Column(length = 30)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private EmailStatus status;

  private String fcmCode;

  public void updateStatus(EmailStatus newStatus) {
    this.status = newStatus;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateBagId(Integer bagId) {
    this.bagId = bagId;
  }

  public void updatePassword(String encryptedPassword) {
    this.password = encryptedPassword;
  }

  public MemberPayload toPayload() {
    return new MemberPayload(id, bagId, nickname, email, status);
  }

  public MemberAuthPayload toAuthPayload() {
    return new MemberAuthPayload(id, bagId, nickname, email, password, status);
  }
}
