package com.e205.entity;

import com.e205.log.LoggableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LostItemAuth implements LoggableEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;
  @Getter
  private Integer memberId;
  @JsonIgnore
  @Getter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lost_id")
  private LostItem lostItem;
  @Column(name = "is_read", nullable = false)
  private char read = 'N';

  public LostItemAuth(Integer memberId, LostItem lostItem) {
    this.memberId = memberId;
    this.lostItem = lostItem;
  }

  public void read() {
    read = 'Y';
  }

  public boolean isRead() {
    return read == 'Y';
  }
}
