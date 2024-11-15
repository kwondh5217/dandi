package com.e205.entity;

import com.e205.command.LostItemSaveCommand;
import com.e205.log.LoggableEntity;
import com.e205.payload.LostItemPayload;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Entity
public class LostItem implements LoggableEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;
  private Integer memberId;
  private Integer startRouteId;
  private Integer endRouteId;
  private String situationDescription;
  private String itemDescription;
  private LocalDateTime lostAt;
  private LocalDateTime createdAt;
  private LocalDateTime endedAt;

  @Builder
  public LostItem(Integer memberId, Integer startRouteId, Integer endRouteId,
      String situationDescription, String itemDescription, LocalDateTime lostAt) {
    this.memberId = memberId;
    this.startRouteId = startRouteId;
    this.endRouteId = endRouteId;
    this.situationDescription = situationDescription;
    this.itemDescription = itemDescription;
    this.lostAt = lostAt;
    this.createdAt = LocalDateTime.now();
  }

  public LostItem(Integer id) {
    this.id = id;
  }

  public LostItem(LostItemSaveCommand command) {
    this.memberId = command.lostMemberId();
    this.situationDescription = command.situationDesc();
    this.itemDescription = command.itemDesc();
    this.startRouteId = command.startRouteId();
    this.endRouteId = command.endRouteId();
    this.lostAt = command.lostAt();
    this.createdAt = LocalDateTime.now();
  }

  public LostItemPayload toPayload() {
    return LostItemPayload.builder()
        .id(id)
        .memberId(memberId)
        .startRouteId(startRouteId)
        .endRouteId(endRouteId)
        .situationDescription(situationDescription)
        .itemDescription(itemDescription)
        .lostAt(lostAt)
        .createdAt(createdAt)
        .endedAt(endedAt)
        .build();
  }

  public void end() {
    endedAt = LocalDateTime.now();
  }

  public boolean isEnded() {
    return endedAt != null;
  }
}
