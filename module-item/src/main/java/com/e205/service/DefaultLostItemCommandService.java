package com.e205.service;

import com.e205.command.LostItemDeleteCommand;
import com.e205.command.LostItemGrantCommand;
import com.e205.command.LostItemSaveCommand;
import com.e205.entity.LostImage;
import com.e205.entity.LostItem;
import com.e205.entity.LostItemAuth;
import com.e205.event.LostItemSaveEvent;
import com.e205.message.ItemEventPublisher;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.LostItemAuthRepository;
import com.e205.repository.LostItemCommandRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DefaultLostItemCommandService implements LostItemCommandService {

  private static final int MAX_IMAGE_COUNT = 3;
  private static final int LOST_ITEM_COOL_TIME = 24;

  private final LostItemCommandRepository lostItemCommandRepository;
  private final ItemEventPublisher eventPublisher;
  private final ImageService imageService;
  private final ItemImageRepository itemImageRepository;
  private final LostItemAuthRepository lostItemAuthRepository;

  @Override
  public void save(LostItemSaveCommand command) {
    verifyImageCount(command);
    verifyCoolTime(command);

    List<String> savedImages = new ArrayList<>();
    try {
      command.images().stream().map(imageService::save).forEach(savedImages::add);
      LostItem lostItem = saveLostItem(command);

      savedImages.stream()
          .map(name -> toLostImage(name, lostItem))
          .forEach(itemImageRepository::save);

      grant(new LostItemGrantCommand(lostItem.getMemberId(), lostItem.getId()));

      eventPublisher.publish(new LostItemSaveEvent(lostItem.toPayload(), LocalDateTime.now()));
    } catch (Exception e) {
      savedImages.forEach(imageService::delete);
      throw e;
    }
  }

  @Override
  public void grant(LostItemGrantCommand command) {
    if (!isExistsLostItem(command.lostId())) {
      throw new RuntimeException("분실물이 존재하지 않습니다.");
    }

    if (!isExistsAuth(command)) {
      lostItemAuthRepository.save(
          new LostItemAuth(command.memberId(), new LostItem(command.lostId())));
    }
  }

  private boolean isExistsAuth(LostItemGrantCommand command) {
    return lostItemAuthRepository.existsByMemberIdAndLostItemId(command.memberId(),
        command.lostId());
  }

  private boolean isExistsLostItem(int lostId) {
    return lostItemCommandRepository.exists(Example.of(new LostItem(lostId)));
  }

  @Override
  public void delete(LostItemDeleteCommand command) {
    lostItemCommandRepository.deleteById(command.lostId());
  }

  private LostImage toLostImage(String imageName, LostItem lostItem) {
    String baseName = FilenameUtils.getBaseName(imageName);
    String type = FilenameUtils.getExtension(imageName);
    return new LostImage(UUID.fromString(baseName), type, lostItem);
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
          throw new RuntimeException("제한 시간 내에 재등록했을 때");
        });
  }

  private static void verifyImageCount(LostItemSaveCommand command) {
    if (command.images().size() > MAX_IMAGE_COUNT) {
      throw new RuntimeException("최대 이미지 개수를 초과했을 때");
    }
  }
}
