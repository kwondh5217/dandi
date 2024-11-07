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
  private final ItemImageRepository itemImageRepository;
  private final LostItemAuthRepository lostItemAuthRepository;

  @Override
  public void save(LostItemSaveCommand command) {
    verifyImageCount(command);
    verifyCoolTime(command);

    LostItem lostItem = saveLostItem(command);
    command.images().stream()
        .map(FilenameUtils::getBaseName)
        .map(UUID::fromString)
        .forEach(image -> saveImage(lostItem, image));

    grant(new LostItemGrantCommand(lostItem.getMemberId(), lostItem.getId()));

    eventPublisher.publish(new LostItemSaveEvent(lostItem.toPayload(), LocalDateTime.now()));
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

  private LostItem saveLostItem(LostItemSaveCommand command) {
    LostItem lostItem = new LostItem(command);
    return lostItemCommandRepository.save(lostItem);
  }

  private void verifyCoolTime(LostItemSaveCommand command) {
    lostItemCommandRepository.findFirstByMemberIdOrderByCreatedAtDesc(command.lostMemberId())
        .filter(recent -> recent.getCreatedAt()
            .isAfter(LocalDateTime.now().minusHours(LOST_ITEM_COOL_TIME)))
        .ifPresent(recent -> {
          throw new RuntimeException("제한 시간 내에 재등록했을 때");
        });
  }

  private LostImage saveImage(LostItem item, UUID imageId) {
    LostImage image = itemImageRepository.findLostImageById(imageId)
        .orElseThrow(() -> new RuntimeException("이미지가 존재하지 않습니다."));
    image.setLostItem(item);
    return image;
  }

  private static void verifyImageCount(LostItemSaveCommand command) {
    if (command.images().size() > MAX_IMAGE_COUNT) {
      throw new RuntimeException("최대 이미지 개수를 초과했을 때");
    }
  }
}
