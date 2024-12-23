package com.e205.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.base.item.FoundItemType;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizSolver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({DefaultFoundItemQueryRepository.class, DefaultItemImageRepository.class})
class FoundItemQueryRepositoryTest {

  @Autowired
  private FoundItemCommandRepository foundItemCommandRepository;
  @Autowired
  private FoundItemQueryRepository foundItemQueryRepository;
  @Autowired
  private QuizSolverRepository quizSolverRepository;
  @Autowired
  private ItemImageRepository itemImageRepository;
  @Autowired
  private QuizCommandRepository quizCommandRepository;

  @DisplayName("사용자 습득물 중 종료되지 않은 것만 조회")
  @Test
  void Verify_MembersFoundItemQuery() {
    // given
    List<FoundItem> endedFoundItem = Stream.generate(() -> generateFoundItem(1))
        .limit(3)
        .peek(FoundItem::end)
        .toList();
    List<FoundItem> notEndedFoundItem = Stream.generate(() -> generateFoundItem(1))
        .limit(3)
        .toList();
    foundItemCommandRepository.saveAll(endedFoundItem);
    foundItemCommandRepository.saveAll(notEndedFoundItem);

    // when
    List<FoundItem> result = foundItemQueryRepository.findAllByMemberId(1);

    // then
    assertThat(result).isNotEmpty()
        .size().isEqualTo(3);
    assertThat(result).noneMatch(FoundItem::isEnded);
  }

  @DisplayName("사용자가 읽을 수 있는 습득물을 조회한다.")
  @Test
  void Verify_findReadable() {
    // given
    int memberId = 1;
    int solver = 2;
    int quizSize = 3;

    List<FoundItem> foundItems = Stream.generate(() -> generateFoundItem(memberId))
        .limit(quizSize)
        .peek(foundItemCommandRepository::save)
        .toList();

    foundItems.stream()
        .skip(1)
        .map(found -> new FoundImage(UUID.randomUUID(), "png", found))
        .peek(itemImageRepository::save)
        .map(image -> new Quiz(image.getFoundItem(), image))
        .peek(quizCommandRepository::save)
        .forEach(quiz -> solveQuiz(solver, quiz));

    // when
    List<FoundItem> readable = foundItemQueryRepository.findReadable(solver);

    // then
    assertThat(readable).isNotEmpty()
        .size().isEqualTo(quizSize - 1);
  }

  private void solveQuiz(Integer memberId, Quiz quiz) {
    quizSolverRepository.save(new QuizSolver(quiz, memberId, true));
  }

  private FoundItem generateFoundItem(Integer memberId) {
    return FoundItem.builder()
        .foundAt(LocalDateTime.now())
        .lat(1D)
        .lon(1D)
        .type(FoundItemType.OTHER)
        .description("묘사")
        .savePlace("저장")
        .memberId(memberId)
        .build();
  }
}