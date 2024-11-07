package com.e205.domain.item.entity;

import com.e205.command.item.payload.ItemPayload;
import com.e205.common.audit.BaseTime;
import com.e205.log.LoggableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Entity
public class Item extends BaseTime implements LoggableEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;

  @Column(nullable = false)
  private Integer memberId;

  @Column(nullable = false, length = 10)
  private String emoticon;

  @Column(nullable = false, length = 20)
  private String name;

  @Column(nullable = false)
  private byte colorKey;

  @Column(nullable = false)
  private byte itemOrder;

  public void updateEmoticon(String emoticon) {
    this.emoticon = emoticon;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateColorKey(byte colorKey) {
    this.colorKey = colorKey;
  }

  public void updateOrder(byte order) {
    this.itemOrder = order;
  }

  public ItemPayload toPayload() {
    return new ItemPayload(id, memberId, emoticon, name, colorKey, itemOrder);
  }
}