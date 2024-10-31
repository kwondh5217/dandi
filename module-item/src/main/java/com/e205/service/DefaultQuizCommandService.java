package com.e205.service;

import com.e205.command.QuizMakeCommand;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizImage;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.QuizCommandRepository;
import com.e205.repository.QuizImageRepository;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DefaultQuizCommandService implements QuizCommandService {

  private static final Integer QUIZ_CANDIDATE_COUNT = 20;
  private static final Integer QUIZ_OPTION_COUNT = 4;
  private static final Random RANDOM = new Random();

  private final QuizCommandRepository quizCommandRepository;
  private final QuizImageRepository quizImageRepository;
  private final ItemImageRepository imageRepository;
  private final FoundItemQueryRepository foundItemQueryRepository;

  @Override
  public void make(QuizMakeCommand command) {
    FoundItem foundItem = getFoundItem(command.foundItemId(), command.memberId());
    FoundImage answer = getFoundImage(command.answerId());

    Quiz quiz = makeQuiz(foundItem, answer);

    List<FoundImage> candidates = getCandidates(command.answerId());

    RANDOM.ints(0, candidates.size()).limit(QUIZ_OPTION_COUNT - 1L)
        .mapToObj(candidates::get).map(image -> new QuizImage(quiz, image))
        .forEach(quizImageRepository::save);
  }

  private FoundImage getFoundImage(UUID imageId) {
    return imageRepository.findFoundImageById(imageId)
        .orElseThrow(() -> new RuntimeException("이미지가 존재하지 않습니다."));
  }

  private Quiz makeQuiz(FoundItem foundItem, FoundImage answer) {
    Quiz quiz = new Quiz(foundItem, answer);
    quizCommandRepository.save(quiz);
    quizImageRepository.save(new QuizImage(quiz, answer));
    return quiz;
  }

  private FoundItem getFoundItem(Integer foundItemId, Integer memberId) {
    FoundItem foundItem = foundItemQueryRepository.findById(foundItemId)
        .orElseThrow(() -> new RuntimeException("습득물이 존재하지 않습니다."));

    if (!foundItem.getMemberId().equals(memberId)) {
      throw new RuntimeException("퀴즈를 생성할 권한이 없습니다.");
    }

    return foundItem;
  }

  private List<FoundImage> getCandidates(UUID answerId) {
    List<FoundImage> candidates = imageRepository.findTopFoundImagesByCreateAtDesc(
        QUIZ_CANDIDATE_COUNT);

    if (candidates.size() < QUIZ_CANDIDATE_COUNT) {
      throw new RuntimeException("습득물의 이미지가 부족해서 퀴즈를 낼 수 없습니다.");
    }

    return candidates.stream()
        .filter(candidate -> !candidate.getId().equals(answerId)).toList();
  }
}
