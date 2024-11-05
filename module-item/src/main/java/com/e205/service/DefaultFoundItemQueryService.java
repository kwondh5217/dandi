package com.e205.service;

import com.e205.entity.QuizSolver;
import com.e205.payload.FoundItemPayload;
import com.e205.payload.ItemImagePayload;
import com.e205.query.FoundItemQuery;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.QuizSolverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DefaultFoundItemQueryService implements FoundItemQueryService {

  private final QuizSolverRepository quizSolverRepository;
  private final ItemImageRepository imageRepository;


  @Override
  public FoundItemPayload find(FoundItemQuery query) {
    QuizSolver solver = getQuizSolver(query);

    if (!solver.isSolved()) {
      throw new RuntimeException("습득물을 조회할 권한이 없습니다.");
    }

    return solver.getQuiz().getFoundItem().toPayload();
  }

  @Override
  public ItemImagePayload findFoundItemImage(Integer foundId) {
    return imageRepository.findByFoundItemId(foundId)
        .map(foundImage -> new ItemImagePayload(foundImage.getName()))
        .orElseThrow(() -> new RuntimeException("분실물의 이미지가 존재하지 않습니다."));
  }

  private QuizSolver getQuizSolver(FoundItemQuery query) {
    return quizSolverRepository.findByMemberIdAndFoundId(
        query.memberId(), query.foundId()).orElseThrow(() -> new RuntimeException("퀴즈를 풀지 않았습니다."));
  }
}
