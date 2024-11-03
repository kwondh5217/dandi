package com.e205.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role_type", length = 10)
@Entity
public class Image {

  @Id
  @Column(columnDefinition = "binary(16)")
  private UUID id;
  @Column(nullable = false, length = 4)
  private String type;
  @Transient
  private String name;

  public Image(UUID id, String type) {
    this.id = id;
    this.type = type;
    this.name = String.format("%s.%s", id.toString(), type);
  }
}
