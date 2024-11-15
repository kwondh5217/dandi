package com.e205.service;

import com.e205.command.FoundItemDeleteCommand;
import com.e205.command.FoundItemSaveCommand;
import com.e205.command.QuizMakeCommand;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.event.FoundItemSaveEvent;
import com.e205.events.EventPublisher;
import com.e205.exception.ItemError;
import com.e205.repository.FoundItemCommandRepository;
import com.e205.repository.ItemImageRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class DefaultFoundItemCommandService implements FoundItemCommandService {

  private final FoundItemCommandRepository foundItemCommandRepository;
  private final ItemImageRepository itemImageRepository;
  private final QuizCommandService quizCommandService;
  private final EventPublisher eventPublisher;

  @Override
  public void save(FoundItemSaveCommand command) {
    if (command.foundAt().isAfter(LocalDateTime.now())) {
      throw ItemError.FOUND_AT_FUTURE.getGlobalException();
    }

    switch (command.type()) {
      case CREDIT, ID -> processCard(command);
      case OTHER -> processOther(command);
    }
  }

  @Override
  public void delete(FoundItemDeleteCommand command) {
    foundItemCommandRepository.findById(command.foundId())
        .ifPresent(FoundItem::end);
  }

  private void processOther(FoundItemSaveCommand command) {
    if (command.image() == null) {
      throw ItemError.FOUND_OTHER_REQUIRE_IMAGE.getGlobalException();
    }
    FoundItem foundItem = foundItemCommandRepository.save(new FoundItem(command));

    UUID imageId = UUID.fromString(FilenameUtils.getBaseName(command.image()));
    FoundImage image = saveImage(foundItem, imageId);

    quizCommandService.make(
        new QuizMakeCommand(foundItem.getId(), command.memberId(), image.getId()));

    FoundItemSaveEvent event = new FoundItemSaveEvent(foundItem.toPayload(), LocalDateTime.now());
    eventPublisher.publishAtLeastOnce(event);
  }

  private void processCard(FoundItemSaveCommand command) {
    if (command.image() != null) {
      throw ItemError.FOUND_CARD_NOT_REQUIRE_IMAGE.getGlobalException();
    }
    // TODO <fosong98> 카드 퀴즈는 다음에 고민
    FoundItem foundItem = foundItemCommandRepository.save(new FoundItem(command));
    FoundItemSaveEvent event = new FoundItemSaveEvent(foundItem.toPayload(), LocalDateTime.now());
    eventPublisher.publishAtLeastOnce(event);
  }

  private FoundImage saveImage(FoundItem item, UUID imageId) {
    FoundImage image = itemImageRepository.findFoundImageById(imageId)
        .orElseThrow(ItemError.FOUND_IMAGE_NOT_FOUND::getGlobalException);
    image.setFoundItem(item);
    return image;
  }
}