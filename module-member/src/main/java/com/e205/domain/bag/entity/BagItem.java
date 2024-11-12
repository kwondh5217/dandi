package com.e205.domain.bag.entity;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.log.LoggableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@NoArgsConstructor
@Getter
@Entity
public class BagItem implements LoggableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "bag_id", nullable = false)
  private Integer bagId;

  @Column(name = "item_id", nullable = false)
  private Integer itemId;

  @Column(name = "item_order", nullable = false)
  private Byte itemOrder;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public void updateOrder(Byte order) {
    this.itemOrder = order;
  }

  @Builder
  public BagItem(Integer bagId, Integer itemId, Byte itemOrder, LocalDateTime createdAt) {
    this.bagId = bagId;
    this.itemId = itemId;
    this.itemOrder = itemOrder;
    this.createdAt = createdAt;
  }

  public BagItemPayload toPayload() {
    return new BagItemPayload(id, bagId, itemId, itemOrder);
  }
}