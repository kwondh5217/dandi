package com.e205.item.controller;

import com.e205.auth.helper.AuthHelper;
import com.e205.item.dto.QuizResponse;
import com.e205.item.dto.QuizResultResponse;
import com.e205.item.dto.QuizSubmitRequest;
import com.e205.item.service.QuizService;
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
  private final AuthHelper authHelper;

  @GetMapping
  public ResponseEntity<QuizResponse> getQuiz(
      @PathVariable("foundId") Integer foundId
  ) {
    QuizResponse response = quizService.getQuiz(authHelper.getMemberId(), foundId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{quizId}")
  public ResponseEntity<QuizResultResponse> submitQuiz(
      @PathVariable("quizId") Integer quizId,
      @RequestBody QuizSubmitRequest request
  ) {
    return ResponseEntity.ok(
        new QuizResultResponse(quizService.submitQuiz(authHelper.getMemberId(), quizId, request)));
  }
}
