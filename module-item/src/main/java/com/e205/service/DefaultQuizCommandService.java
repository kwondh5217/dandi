package com.e205.service;

import com.e205.base.item.command.QuizMakeCommand;
import com.e205.base.item.command.QuizSubmitCommand;
import com.e205.base.item.service.QuizCommandService;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizImage;
import com.e205.entity.QuizSolver;
import com.e205.exception.ItemError;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.QuizCommandRepository;
import com.e205.repository.QuizImageRepository;
import com.e205.repository.QuizQueryRepository;
import com.e205.repository.QuizSolverRepository;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultQuizCommandService implements QuizCommandService {

  private static final Integer QUIZ_CANDIDATE_COUNT = 20;
  private static final Integer QUIZ_OPTION_COUNT = 4;
  private static final Random RANDOM = new Random();

  private final QuizCommandRepository quizCommandRepository;
  private final QuizImageRepository quizImageRepository;
  private final ItemImageRepository imageRepository;
  private final FoundItemQueryRepository foundItemQueryRepository;
  private final QuizQueryRepository quizQueryRepository;
  private final QuizSolverRepository quizSolverRepository;

  @Override
  public void make(QuizMakeCommand command) {
    FoundItem foundItem = getFoundItem(command.foundItemId(), command.memberId());
    FoundImage answer = getFoundImage(command.answerId());

    Quiz quiz = makeQuiz(foundItem, answer);

    quizSolverRepository.save(new QuizSolver(quiz, command.memberId(), true));

    List<FoundImage> candidates = getCandidates(command.answerId());

    RANDOM.ints(0, candidates.size()).limit(QUIZ_OPTION_COUNT - 1L).mapToObj(candidates::get)
        .map(image -> new QuizImage(quiz, image)).forEach(quizImageRepository::save);
  }

  @Override
  public void submit(QuizSubmitCommand command) {
    quizSolverRepository.findByMemberIdAndQuizId(command.memberId(), command.quizId())
        .ifPresent(solver -> {
          throw ItemError.FOUND_QUIZ_ALREADY_SOLVED.getGlobalException();
        });

    Quiz quiz = getQuiz(command.quizId());

    boolean isSolved = quiz.getAnswer().getId().equals(command.answerId());

    QuizSolver quizSolver = new QuizSolver(quiz, command.memberId(), isSolved);

    quizSolverRepository.save(quizSolver);
  }

  private Quiz getQuiz(Integer quizId) {
    return quizQueryRepository.findById(quizId)
        .orElseThrow(ItemError.FOUND_QUIZ_NOT_FOUND::getGlobalException);
  }

  private FoundImage getFoundImage(UUID imageId) {
    return imageRepository.findFoundImageById(imageId)
        .orElseThrow(ItemError.FOUND_IMAGE_NOT_FOUND::getGlobalException);
  }

  private Quiz makeQuiz(FoundItem foundItem, FoundImage answer) {
    Quiz quiz = new Quiz(foundItem, answer);
    quizCommandRepository.save(quiz);
    quizImageRepository.save(new QuizImage(quiz, answer));
    return quiz;
  }

  private FoundItem getFoundItem(Integer foundItemId, Integer memberId) {
    FoundItem foundItem = foundItemQueryRepository.findById(foundItemId)
        .orElseThrow(ItemError.FOUND_NOT_EXIST::getGlobalException);

    if (!foundItem.getMemberId().equals(memberId)) {
      throw ItemError.FOUND_QUIZ_NOT_AUTH.getGlobalException();
    }

    if (foundItem.isEnded()) {
      throw ItemError.FOUND_ALREADY_ENDED.getGlobalException();
    }

    return foundItem;
  }

  private List<FoundImage> getCandidates(UUID answerId) {
    List<FoundImage> candidates = imageRepository.findTopFoundImagesByCreateAtDesc(
        QUIZ_CANDIDATE_COUNT);

    if (candidates.size() < QUIZ_CANDIDATE_COUNT) {
      throw ItemError.FOUND_QUIZ_IMAGE_INSUFFICIENT.getGlobalException();
    }

    return candidates.stream().filter(candidate -> !candidate.getId().equals(answerId)).toList();
  }
}
