package com.e205.item.service;

import com.e205.command.QuizSubmitCommand;
import com.e205.item.dto.QuizResponse;
import com.e205.item.dto.QuizSubmitRequest;
import com.e205.payload.QuizPayload;
import com.e205.query.QuizQuery;
import com.e205.service.QuizCommandService;
import com.e205.service.QuizQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class QuizService {

  private final QuizQueryService quizQueryService;
  private final QuizCommandService quizCommandService;

  @Transactional
  public QuizResponse getQuiz(int memberId, int foundItemId) {
    QuizQuery query = new QuizQuery(memberId, foundItemId);
    QuizPayload payload = quizQueryService.findQuiz(query);
    return QuizResponse.from(payload);
  }

  @Transactional
  public boolean submitQuiz(int memberId, int foundId, QuizSubmitRequest request) {
    QuizQuery query = new QuizQuery(memberId, foundId);
    QuizPayload quiz = quizQueryService.findQuiz(query);
    QuizSubmitCommand command = new QuizSubmitCommand(memberId, quiz.id(), request.getAnswerId());
    quizCommandService.submit(command);

    return quizQueryService.getQuizResult(memberId, quiz.id());
  }
}
