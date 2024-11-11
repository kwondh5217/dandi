package com.e205.entity;

import com.e205.payload.CommentPayload;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DiscriminatorValue(value = "lost_item")
@Entity
public class LostComment extends Comment {

  @JoinColumn(name = "lost_item_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private LostItem lostItem;
  @JoinColumn(name = "parent_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private LostComment parent;

  @Builder
  public LostComment(int writerId, String content, LocalDateTime createdAt, LostItem lostItem,
      LostComment parent) {
    super(writerId, content, createdAt);
    this.lostItem = lostItem;
    this.parent = parent;
  }

  public CommentPayload toPayload() {
    Integer parentId = parent != null ? parent.getId() : null;
    return new CommentPayload(getId(), getWriterId(), parentId, getContent(), getCreatedAt());
  }
}
