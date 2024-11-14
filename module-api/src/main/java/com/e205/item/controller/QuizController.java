package com.e205.item.controller;

import com.e205.item.dto.QuizResponse;
import com.e205.item.dto.QuizResultResponse;
import com.e205.item.dto.QuizSubmitRequest;
import com.e205.item.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/founds/{foundId}/quiz")
@RestController
public class QuizController {

  private final QuizService quizService;

  @GetMapping
  public ResponseEntity<QuizResponse> getQuiz(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @PathVariable("foundId") Integer foundId
  ) {
    QuizResponse response = quizService.getQuiz(memberId, foundId);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<QuizResultResponse> submitQuiz(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @PathVariable("foundId") Integer foundId,
      @Valid @RequestBody QuizSubmitRequest request
  ) {
    return ResponseEntity.ok(
        new QuizResultResponse(quizService.submitQuiz(memberId, foundId, request)));
  }
}
