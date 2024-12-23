package com.e205.service;

import com.e205.base.item.service.FoundItemQueryService;
import com.e205.entity.FoundItem;
import com.e205.entity.QuizSolver;
import com.e205.exception.ItemError;
import com.e205.base.item.payload.FoundItemPayload;
import com.e205.base.item.payload.ItemImagePayload;
import com.e205.base.item.query.FoundItemListQuery;
import com.e205.base.item.query.FoundItemQuery;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.QuizSolverRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DefaultFoundItemQueryService implements FoundItemQueryService {

  private final QuizSolverRepository quizSolverRepository;
  private final ItemImageRepository imageRepository;
  private final FoundItemQueryRepository foundItemQueryRepository;

  @Override
  public FoundItemPayload find(FoundItemQuery query) {
    QuizSolver solver = getQuizSolver(query);

    if (!solver.isSolved()) {
      throw ItemError.FOUND_NOT_AUTH.getGlobalException();
    }

    FoundItem foundItem = solver.getQuiz().getFoundItem();
    if (foundItem.isEnded()) {
      throw ItemError.FOUND_ALREADY_ENDED.getGlobalException();
    }

    return foundItem.toPayload();
  }

  @Override
  public ItemImagePayload findFoundItemImage(Integer foundId) {
    return imageRepository.findByFoundItemId(foundId)
        .map(foundImage -> new ItemImagePayload(foundImage.getName()))
        .orElseThrow(ItemError.FOUND_IMAGE_NOT_FOUND::getGlobalException);
  }

  @Override
  public List<FoundItemPayload> findReadable(int memberId) {
    return foundItemQueryRepository.findReadable(memberId).stream()
        .map(FoundItem::toPayload)
        .toList();
  }

  @Override
  public List<FoundItemPayload> find(FoundItemListQuery query) {
    return foundItemQueryRepository.findAllByMemberId(query.memberId()).stream()
        .map(FoundItem::toPayload).toList();
  }

  private QuizSolver getQuizSolver(FoundItemQuery query) {
    return quizSolverRepository.findByMemberIdAndFoundId(query.memberId(), query.foundId())
        .orElseThrow(ItemError.FOUND_QUIZ_NOT_SOLVED::getGlobalException);
  }
}
