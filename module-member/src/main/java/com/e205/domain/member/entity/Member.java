package com.e205.domain.member.entity;

import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberAuthPayload;
import com.e205.command.member.payload.MemberStatus;
import com.e205.log.LoggableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member implements LoggableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private Integer bagId;

  @Column(nullable = false, length = 15)
  private String nickname;

  @Column(length = 70)
  private String password;

  @Column(length = 30)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private EmailStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MemberStatus memberStatus;

  private String fcmToken;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private boolean foundItemAlarm;

  @Column(nullable = false)
  private boolean lostItemAlarm;

  @Column(nullable = false)
  private boolean commentAlarm;

  public void updateFoundItemAlarm(boolean foundItemAlarm) {
    this.foundItemAlarm = foundItemAlarm;
  }

  public void updateCommentAlarm(boolean commentAlarm) {
    this.commentAlarm = commentAlarm;
  }

  public void updateLostItemAlarm(boolean lostItemAlarm) {
    this.lostItemAlarm = lostItemAlarm;
  }

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

  public void updateFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }

  public void updateMemberStatus(MemberStatus memberStatus) {
    this.memberStatus = memberStatus;
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public MemberPayload toPayload() {
    return new MemberPayload(id, bagId, nickname, email, status, memberStatus, fcmToken,
        foundItemAlarm, lostItemAlarm, commentAlarm);
  }

  public MemberAuthPayload toAuthPayload() {
    return new MemberAuthPayload(id, bagId, nickname, email, password, status, memberStatus);
  }
}