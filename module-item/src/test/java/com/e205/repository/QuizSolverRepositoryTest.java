package com.e205.repository;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import com.e205.base.item.FoundItemType;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizSolver;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(DefaultItemImageRepository.class)
class QuizSolverRepositoryTest {

  @Autowired
  QuizSolverRepository quizSolverRepository;

  @Autowired
  FoundItemCommandRepository foundItemCommandRepository;

  @Autowired
  QuizCommandRepository quizCommandRepository;

  @Autowired
  ItemImageRepository imageRepository;

  @DisplayName("사용자 아이디와 습득물 아이디로 퀴즈 풀이 여부를 조회할 수 있다.")
  @Test
  void findQuizSolverWithMemberIdAndFoundId() {
    // given
    Integer memberId = 1;
    Integer solverId = 2;

    FoundItem foundItem = new FoundItem(memberId, 1d, 1d, "", "", FoundItemType.OTHER, now(), "주소없음");
    foundItemCommandRepository.save(foundItem);

    FoundImage image = new FoundImage(UUID.randomUUID(), "png", foundItem);
    imageRepository.save(image);

    Quiz quiz = new Quiz(foundItem, image);
    quizCommandRepository.save(quiz);

    QuizSolver quizSolver = new QuizSolver(quiz, solverId, false);
    quizSolverRepository.save(quizSolver);

    // when
    Optional<QuizSolver> result = quizSolverRepository.findByMemberIdAndFoundId(
        solverId, foundItem.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(quizSolver);
  }
}