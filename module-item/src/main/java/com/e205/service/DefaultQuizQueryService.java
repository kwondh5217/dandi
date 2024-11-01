package com.e205.service;

import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.query.QuizQuery;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.QuizQueryRepository;
import com.e205.repository.QuizSolverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DefaultQuizQueryService implements QuizQueryService {

  private final FoundItemQueryRepository foundItemQueryRepository;
  private final QuizSolverRepository quizSolverRepository;
  private final QuizQueryRepository quizQueryRepository;

  @Transactional(readOnly = true)
  @Override
  public Quiz findQuiz(QuizQuery query) {
    FoundItem foundItem = foundItemQueryRepository.findById(query.foundId())
        .orElseThrow(() -> new RuntimeException("습득물이 존재하지 않습니다."));

    if (foundItem.getMemberId().equals(query.memberId())) {
      throw new RuntimeException("습득물을 등록한 사람은 퀴즈를 풀 수 없습니다.");
    }

    Quiz quiz = quizQueryRepository.findByFoundItemId(foundItem.getId())
        .orElseThrow(() -> new RuntimeException("퀴즈가 존재하지 않습니다."));

    quizSolverRepository.findByMemberIdAndQuizId(query.memberId(), quiz.getId())
        .ifPresent(solver -> {
          throw new RuntimeException("이미 퀴즈를 풀었습니다.");
        });

    return quiz;
  }

}