package com.e205.domain.bag.entity;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.common.audit.BaseTime;
import com.e205.log.LoggableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class BagItem extends BaseTime implements LoggableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "bag_id", nullable = false)
  private Integer bagId;

  @Column(name = "item_id", nullable = false)
  private Integer itemId;

  @Column(name = "item_order", nullable = false)
  private Byte itemOrder;

  public void updateOrder(Byte order) {
    this.itemOrder = order;
  }

  @Builder
  public BagItem(Integer bagId, Integer itemId, Byte itemOrder) {
    this.bagId = bagId;
    this.itemId = itemId;
    this.itemOrder = itemOrder;
  }

  public BagItemPayload toPayload() {
    return new BagItemPayload(id, bagId, itemId, itemOrder);
  }
}