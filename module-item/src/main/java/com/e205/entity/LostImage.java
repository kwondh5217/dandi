package com.e205.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("lost_item")
public class LostImage extends Image {

  @ManyToOne
  @JoinColumn(name = "lost_id")
  private LostItem lostItem;

  public LostImage(UUID name, String type, LostItem lostItem) {
    super(name, type);
    this.lostItem = lostItem;
  }
}
