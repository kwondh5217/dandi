package com.e205.service;

import com.e205.entity.LostImage;
import com.e205.entity.LostItem;
import com.e205.events.EventPublisher;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.LostItemCommandRepository;
import com.e205.command.LostItemSaveCommand;
import com.e205.event.LostItemSaveEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DefaultLostItemCommandService implements LostItemCommandService {

  private static final int MAX_IMAGE_COUNT = 3;
  private static final int LOST_ITEM_COOL_TIME = 24;

  private final LostItemCommandRepository lostItemCommandRepository;
  private final EventPublisher eventPublisher;
  private final ImageService imageService;
  private final ItemImageRepository itemImageRepository;

  @Transactional
  @Override
  public void save(LostItemSaveCommand command) {
    verifyImageCount(command);
    verifyCoolTime(command);

    List<String> savedImages = new ArrayList<>();
    try {
      command.images().stream().map(imageService::save).forEach(savedImages::add);
      LostItem lostItem = saveLostItem(command);
      savedImages.stream()
          .map(name -> name.split("\\."))
          .map(name -> new LostImage(UUID.fromString(name[0]), name[1], lostItem))
          .forEach(itemImageRepository::save);

      eventPublisher.publish(new LostItemSaveEvent(lostItem.toPayload(), LocalDateTime.now()));
    } catch (Exception e) {
      savedImages.forEach(imageService::delete);
      throw e;
    }
  }

  private LostItem saveLostItem(LostItemSaveCommand command) {
    LostItem lostItem = new LostItem(command);
    lostItemCommandRepository.save(lostItem);
    return lostItem;
  }

  private void verifyCoolTime(LostItemSaveCommand command) {
    lostItemCommandRepository.findFirstByMemberIdOrderByCreatedAtDesc(command.lostMemberId())
        .filter(recent -> recent.getCreatedAt()
            .isAfter(LocalDateTime.now().minusHours(LOST_ITEM_COOL_TIME)))
        .ifPresent(recent -> {
          // TODO <fosong98> 예외 구체화 필요
          throw new RuntimeException("제한 시간 내에 재등록했을 때");
        });
  }

  private static void verifyImageCount(LostItemSaveCommand command) {
    if (command.images().size() > MAX_IMAGE_COUNT) {
      // TODO <fosong98> 예외 구체화 필요
      throw new RuntimeException("최대 이미지 개수를 초과했을 때");
    }
  }
}
