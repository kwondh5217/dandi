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
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@DiscriminatorValue("lost_item")
public class LostImage extends Image implements LoggableEntity {

  @JsonIgnore
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
