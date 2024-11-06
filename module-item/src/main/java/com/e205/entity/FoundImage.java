package com.e205.entity;

import com.e205.log.LoggableEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("found_item")
public class FoundImage extends Image implements LoggableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "found_id")
  private FoundItem foundItem;
  private LocalDateTime createdAt;

  public FoundImage(UUID id, String type, FoundItem foundItem) {
    super(id, type);
    this.foundItem = foundItem;
    this.createdAt = LocalDateTime.now();
  }
}
