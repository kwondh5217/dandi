package com.e205.item.dto;

import com.e205.payload.QuizImagePayload;

public record QuizOptionResponse(
    String image,
    String description
) {

  public static QuizOptionResponse from(QuizImagePayload payload) {
    return new QuizOptionResponse(payload.image(), payload.description());
  }
}
