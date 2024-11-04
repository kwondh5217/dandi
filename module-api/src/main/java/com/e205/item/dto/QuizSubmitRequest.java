package com.e205.item.dto;

import java.util.UUID;

public record QuizSubmitRequest(
    String answer
) {

  public UUID getAnswerId() {
    return UUID.fromString(answer.split("\\.")[0]);
  }
}
