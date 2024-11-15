package com.e205.entity;

import com.e205.log.LoggableEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@DiscriminatorValue("found_item")
public class FoundImage extends Image implements LoggableEntity {

  @JsonIgnore
  @Setter
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
