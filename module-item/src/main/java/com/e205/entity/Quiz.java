package com.e205.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Quiz {

  @GeneratedValue
  @Id
  private Integer id;
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "found_id")
  private FoundItem foundItem;
  @OneToOne(fetch = FetchType.LAZY)
  private FoundImage answer;

  public Quiz(FoundItem foundItem, FoundImage answer) {
    this.foundItem = foundItem;
    this.answer = answer;
  }
}
