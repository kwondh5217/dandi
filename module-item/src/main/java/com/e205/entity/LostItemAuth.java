package com.e205.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LostItemAuth {

  @GeneratedValue
  @Id
  private Integer id;
  private Integer memberId;
  @Getter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lost_id")
  private LostItem lostItem;
  @Column(nullable = false)
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
