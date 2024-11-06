package com.e205.byteBuddy;

import com.e205.log.LoggableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class OutboxEvent implements LoggableEntity {

  @Id
  private String eventId;
  @Enumerated(EnumType.STRING)
  private EventStatus status = EventStatus.PENDING;
  @Lob
  private String payload;
  private LocalDateTime time;
  private String eventType;

  public void complete() {
    status = EventStatus.COMPLETED;
  }
}
