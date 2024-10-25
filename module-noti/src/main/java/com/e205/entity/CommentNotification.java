package com.e205.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@Setter
@Getter
@Entity
@DiscriminatorValue("comment")
public class CommentNotification extends Notification {
  private Integer commentId;
}