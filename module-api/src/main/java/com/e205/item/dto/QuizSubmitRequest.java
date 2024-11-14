package com.e205.item.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record QuizSubmitRequest(
    @NotBlank
    String answer
) {

  public UUID getAnswerId() {
    return UUID.fromString(answer.split("\\.")[0]);
  }
}
