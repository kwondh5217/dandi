package com.e205.entity;

import com.e205.FoundItemType;
import com.e205.command.FoundItemSaveCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FoundItem {

  @GeneratedValue
  @Id
  private Integer id;
  private Integer memberId;
  @Column(precision = 9, scale = 6)
  private Double lat;
  @Column(precision = 9, scale = 6)
  private Double lon;
  private String description;
  private String savePlace;
  @Enumerated(value = EnumType.STRING)
  private FoundItemType type;
  private LocalDateTime foundAt;
  private LocalDateTime createdAt;

  @Builder
  public FoundItem(Integer memberId, Double lat, Double lon, String description, String savePlace,
      FoundItemType type, LocalDateTime foundAt) {
    this.memberId = memberId;
    this.lat = lat;
    this.lon = lon;
    this.description = description;
    this.savePlace = savePlace;
    this.type = type;
    this.foundAt = foundAt;
    this.createdAt = LocalDateTime.now();
  }

  public FoundItem(FoundItemSaveCommand command) {
    this.memberId = command.memberId();
    this.lat = command.location().getX();
    this.lon = command.location().getY();
    this.description = command.itemDesc();
    this.savePlace = command.storageDesc();
    this.type = command.type();
    this.foundAt = command.foundAt();
    this.createdAt = LocalDateTime.now();
  }
}
