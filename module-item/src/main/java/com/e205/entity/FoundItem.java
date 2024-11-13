package com.e205.entity;

import com.e205.FoundItemType;
import com.e205.command.FoundItemSaveCommand;
import com.e205.log.LoggableEntity;
import com.e205.payload.FoundItemPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class FoundItem implements LoggableEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;
  private Integer memberId;
  @Column(precision = 9)
  private Double lat;
  @Column(precision = 9)
  private Double lon;
  private String address;
  private String description;
  private String savePlace;
  @Enumerated(value = EnumType.STRING)
  private FoundItemType type;
  private LocalDateTime foundAt;
  private LocalDateTime createdAt;
  private LocalDateTime endedAt;

  @Builder
  public FoundItem(Integer memberId, Double lat, Double lon, String description, String savePlace,
      FoundItemType type, LocalDateTime foundAt, String address) {
    this.memberId = memberId;
    this.lat = lat;
    this.lon = lon;
    this.description = description;
    this.savePlace = savePlace;
    this.type = type;
    this.foundAt = foundAt;
    this.createdAt = LocalDateTime.now();
    this.address = address;
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
    this.address = command.address();
  }

  public FoundItemPayload toPayload() {
    return FoundItemPayload.builder()
        .id(id)
        .memberId(memberId)
        .lat(lat)
        .lon(lon)
        .description(description)
        .savePlace(savePlace)
        .type(type)
        .address(address)
        .foundAt(foundAt)
        .build();
  }

  public boolean isEnded() {
    return endedAt != null;
  }

  public void end() {
    if (!isEnded())
      endedAt = LocalDateTime.now();
  }
}
