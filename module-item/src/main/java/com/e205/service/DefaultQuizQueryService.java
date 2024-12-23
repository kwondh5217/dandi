package com.e205.service;

import com.e205.base.item.service.QuizQueryService;
import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizImage;
import com.e205.entity.QuizSolver;
import com.e205.exception.ItemError;
import com.e205.base.item.payload.QuizImagePayload;
import com.e205.base.item.payload.QuizPayload;
import com.e205.base.item.query.QuizQuery;
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
      throw ItemError.FOUND_ALREADY_ENDED.getGlobalException();
    }

    if (foundItem.getMemberId().equals(query.memberId())) {
      throw ItemError.FOUND_QUIZ_OWNER_CANNOT_SOLVE.getGlobalException();
    }

    Quiz quiz = quizQueryRepository.findByFoundItemId(foundItem.getId())
        .orElseThrow(ItemError.FOUND_QUIZ_NOT_FOUND::getGlobalException);

    quizSolverRepository.findByMemberIdAndQuizId(query.memberId(), quiz.getId())
        .ifPresent(solver -> {
          throw ItemError.FOUND_QUIZ_ALREADY_SOLVED.getGlobalException();
        });

    List<QuizImagePayload> options = quizImageRepository.findQuizImagesByQuizId(quiz.getId())
        .stream()
        .map(QuizImage::toPayload)
        .toList();

    return new QuizPayload(quiz.getId(), foundItem.getId(), options);
  }

  @Override
  public boolean getQuizResult(Integer memberId, Integer quizId) {
    return quizSolverRepository.findByMemberIdAndQuizId(memberId, quizId)
        .map(QuizSolver::isSolved)
        .orElseThrow(ItemError.FOUND_QUIZ_NOT_FOUND::getGlobalException);
  }

}
