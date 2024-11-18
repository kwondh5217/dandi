package com.e205.cdc;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class BinlogPosition {

  @Id
  private Integer id = 1;
  private String binlogFileName;
  private Long binlogPosition;
  private LocalDateTime updatedAt;

  @PrePersist
  @PreUpdate
  public void updateTimestamp() {
    this.updatedAt = LocalDateTime.now();
  }
}