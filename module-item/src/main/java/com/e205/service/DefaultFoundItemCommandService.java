package com.e205.service;

import com.e205.command.FoundItemDeleteCommand;
import com.e205.command.FoundItemSaveCommand;
import com.e205.command.QuizMakeCommand;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.event.FoundItemSaveEvent;
import com.e205.message.ItemEventPublisher;
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
  private final ImageService imageService;
  private final QuizCommandService quizCommandService;
  private final ItemEventPublisher eventPublisher;

  @Override
  public void save(FoundItemSaveCommand command) {
    if (command.foundAt().isAfter(LocalDateTime.now())) {
      throw new RuntimeException("습득 날짜가 미래입니다.");
    }

    switch (command.type()) {
      case CREDIT, ID -> processCard(command);
      case OTHER -> processOther(command);
    }
  }

  @Override
  public void delete(FoundItemDeleteCommand command) {
    foundItemCommandRepository.deleteById(command.foundId());
  }

  private void processOther(FoundItemSaveCommand command) {
    if (command.image() == null) {
      throw new RuntimeException("이미지는 필수입니다.");
    }
    String imageName = imageService.save(command.image());

    try {
      FoundItem foundItem = foundItemCommandRepository.save(new FoundItem(command));
      UUID imageId = UUID.fromString(FilenameUtils.getBaseName(imageName));
      String type = FilenameUtils.getExtension(imageName);
      FoundImage image = itemImageRepository.save(new FoundImage(imageId, type, foundItem));
      quizCommandService.make(
          new QuizMakeCommand(foundItem.getId(), command.memberId(), image.getId()));

      FoundItemSaveEvent event = new FoundItemSaveEvent(foundItem.toPayload(),
          LocalDateTime.now());
      eventPublisher.publish(event);
    } catch (Exception e) {
      imageService.delete(imageName);
      throw e;
    }
  }

  private void processCard(FoundItemSaveCommand command) {
    if (command.image() != null) {
      throw new RuntimeException("카드나 신분증 사진이 포함되어 있습니다.");
    }
    // TODO <fosong98> 퀴즈는 다음에 고민
    FoundItem foundItem = foundItemCommandRepository.save(new FoundItem(command));
    FoundItemSaveEvent event = new FoundItemSaveEvent(foundItem.toPayload(),
        LocalDateTime.now());
    eventPublisher.publish(event);
  }
}