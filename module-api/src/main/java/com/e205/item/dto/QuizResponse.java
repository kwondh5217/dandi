package com.e205.item.dto;

import com.e205.payload.QuizPayload;
import java.util.List;

public record QuizResponse(
    Integer id,
    Integer foundId,
    List<QuizOptionResponse> options
) {

  public static QuizResponse from(QuizPayload payload) {
    List<QuizOptionResponse> options = payload.options().stream()
        .map(QuizOptionResponse::from)
        .toList();
    return new QuizResponse(payload.id(), payload.foundId(), options);
  }
}
