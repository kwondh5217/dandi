package com.e205.item.controller;

import com.e205.item.dto.QuizResponse;
import com.e205.item.dto.QuizResultResponse;
import com.e205.item.dto.QuizSubmitRequest;
import com.e205.item.service.QuizService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
      Principal principal,
      @PathVariable("foundId") Integer foundId
  ) {
    int memberId = Integer.parseInt(principal.getName());
    QuizResponse response = quizService.getQuiz(memberId, foundId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{quizId}")
  public ResponseEntity<QuizResultResponse> submitQuiz(
      Principal principal,
      @PathVariable("quizId") Integer quizId,
      @RequestBody QuizSubmitRequest request
  ) {
    int memberId = Integer.parseInt(principal.getName());
    return ResponseEntity.ok(
        new QuizResultResponse(quizService.submitQuiz(memberId, quizId, request)));
  }
}
