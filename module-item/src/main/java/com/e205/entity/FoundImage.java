package com.e205.entity;

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
@DiscriminatorValue("found_item")
public class FoundImage extends Image {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "found_id")
  private FoundItem foundItem;

  public FoundImage(UUID id, String type, FoundItem foundItem) {
    super(id, type);
    this.foundItem = foundItem;
  }
}
