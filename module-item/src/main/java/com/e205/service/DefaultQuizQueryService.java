package com.e205.service;

import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizImage;
import com.e205.exception.ItemError;
import com.e205.payload.QuizImagePayload;
import com.e205.payload.QuizPayload;
import com.e205.query.QuizQuery;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.QuizImageRepository;
import com.e205.repository.QuizQueryRepository;
import com.e205.repository.QuizSolverRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DefaultQuizQueryService implements QuizQueryService {

  private final FoundItemQueryRepository foundItemQueryRepository;
  private final QuizSolverRepository quizSolverRepository;
  private final QuizQueryRepository quizQueryRepository;
  private final QuizImageRepository quizImageRepository;

  @Transactional(readOnly = true)
  @Override
  public QuizPayload findQuiz(QuizQuery query) {
    FoundItem foundItem = foundItemQueryRepository.findById(query.foundId())
        .orElseThrow(ItemError.FOUND_NOT_EXIST::getGlobalException);

    if (foundItem.isEnded()) {
      ItemError.FOUND_ALREADY_ENDED.throwGlobalException();
    }

    if (foundItem.getMemberId().equals(query.memberId())) {
      ItemError.FOUND_QUIZ_OWNER_CANNOT_SOLVE.throwGlobalException();
    }

    Quiz quiz = quizQueryRepository.findByFoundItemId(foundItem.getId())
        .orElseThrow(ItemError.FOUND_QUIZ_NOT_FOUND::getGlobalException);

    quizSolverRepository.findByMemberIdAndQuizId(query.memberId(), quiz.getId())
        .ifPresent(solver -> {
          ItemError.FOUND_QUIZ_ALREADY_SOLVED.throwGlobalException();
        });

    List<QuizImagePayload> options = quizImageRepository.findQuizImagesByQuizId(quiz.getId())
        .stream()
        .map(QuizImage::toPayload)
        .toList();

    return new QuizPayload(quiz.getId(), foundItem.getId(), options);
  }

}
