package com.e205.entity;

import com.e205.log.LoggableEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("lost_item")
public class LostImage extends Image implements LoggableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lost_id")
  private LostItem lostItem;

  public LostImage(UUID name, String type, LostItem lostItem) {
    super(name, type);
    this.lostItem = lostItem;
  }

  public void setLostItem(LostItem lostItem) {
    this.lostItem = lostItem;
  }
}
