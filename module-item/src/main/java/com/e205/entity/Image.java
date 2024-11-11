package com.e205.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@DiscriminatorColumn(name = "role_type", length = 10)
@Entity
public class Image {

  @Id
  @Column(columnDefinition = "binary(16)")
  private UUID id;
  @Column(nullable = false, length = 4)
  private String type;

  public Image(UUID id, String type) {
    this.id = id;
    this.type = type;
  }

  public String getName() {
    return String.format("%s.%s", id, type);
  }
}
