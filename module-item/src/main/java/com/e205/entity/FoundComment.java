package com.e205.entity;

import com.e205.payload.CommentPayload;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@DiscriminatorValue(value = "found_item")
@Getter
@Entity
public class FoundComment extends Comment {

  @JoinColumn(name = "found_item_id")
  @ManyToOne
  private FoundItem foundItem;
  @JsonIgnore
  @JoinColumn(name = "parent_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private FoundComment parent;

  @Builder
  public FoundComment(int writerId, String content, LocalDateTime createdAt,
      FoundComment parent, FoundItem foundItem) {
    super(writerId, content, createdAt);
    this.foundItem = foundItem;
    this.parent = parent;
  }

  public CommentPayload toPayload() {
    Integer parentId = parent != null ? parent.getId() : null;
    return new CommentPayload(getId(), foundItem.getId(), getWriterId(), parentId, getContent(),
        getCreatedAt());
  }
}
