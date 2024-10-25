package com.e205.entity;

import com.e205.command.LostItemSaveCommand;
import com.e205.log.LoggableEntity;
import com.e205.payload.LostItemPayload;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class LostItem implements LoggableEntity {

  @GeneratedValue
  @Id
  private Integer id;
  private Integer memberId;
  private LineString route;
  private String situationDescription;
  private String itemDescription;
  private LocalDateTime createdAt;
  private LocalDateTime endedAt;

  @Builder
  public LostItem(Integer memberId, LineString route, String situationDescription,
      String itemDescription) {
    this.memberId = memberId;
    this.route = route;
    this.situationDescription = situationDescription;
    this.itemDescription = itemDescription;
    this.createdAt = LocalDateTime.now();
  }

  public LostItem(LostItemSaveCommand command) {
    this.memberId = command.lostMemberId();
    this.route = command.route();
    this.situationDescription = command.situationDesc();
    this.itemDescription = command.itemDesc();
    this.createdAt = LocalDateTime.now();
  }

  public LostItemPayload toPayload() {
    return LostItemPayload.builder()
        .id(id)
        .memberId(memberId)
        .route(route)
        .situationDescription(situationDescription)
        .itemDescription(itemDescription)
        .createdAt(createdAt)
        .endedAt(endedAt)
        .build();
  }
}
