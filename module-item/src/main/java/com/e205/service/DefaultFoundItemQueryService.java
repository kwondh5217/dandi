package com.e205.service;

import com.e205.entity.QuizSolver;
import com.e205.payload.FoundItemPayload;
import com.e205.query.FoundItemQuery;
import com.e205.repository.QuizSolverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DefaultFoundItemQueryService implements FoundItemQueryService {

  private final QuizSolverRepository quizSolverRepository;

  @Override
  public FoundItemPayload find(FoundItemQuery query) {
    QuizSolver solver = getQuizSolver(query);

    if (!solver.isSolved()) {
      throw new RuntimeException("습득물을 조회할 권한이 없습니다.");
    }

    return solver.getQuiz().getFoundItem().toPayload();
  }

  private QuizSolver getQuizSolver(FoundItemQuery query) {
    return quizSolverRepository.findByMemberIdAndFoundId(
        query.memberId(), query.foundId()).orElseThrow(() -> new RuntimeException("퀴즈를 풀지 않았습니다."));
  }
}
