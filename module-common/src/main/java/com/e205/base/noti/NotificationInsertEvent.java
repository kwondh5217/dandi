package com.e205.base.noti;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class NotificationInsertEvent {
  private Integer memberId;
  private String title;
  private String body;
  private String type;
}