package com.e205.service;

import com.e205.command.LostItemDeleteCommand;
import com.e205.command.LostItemGrantCommand;
import com.e205.command.LostItemSaveCommand;
import com.e205.entity.LostImage;
import com.e205.entity.LostItem;
import com.e205.entity.LostItemAuth;
import com.e205.exception.ItemError;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.LostItemAuthRepository;
import com.e205.repository.LostItemRepository;
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
  private static final int LOST_ITEM_COOL_TIME = 0;

  private final LostItemRepository lostItemRepository;
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
  }

  @Override
  public void grant(LostItemGrantCommand command) {
    if (!isExistsLostItem(command.lostId())) {
      throw ItemError.LOST_NOT_FOUND.getGlobalException();
    }

    if (!isExistsAuth(command)) {
      lostItemAuthRepository.save(
          new LostItemAuth(command.memberId(), new LostItem(command.lostId())));
    }
  }

  @Override
  public void delete(LostItemDeleteCommand command) {
    lostItemRepository.findById(command.lostId())
        .ifPresent(LostItem::end);
  }

  private boolean isExistsAuth(LostItemGrantCommand command) {
    return lostItemAuthRepository.existsByMemberIdAndLostItemId(command.memberId(),
        command.lostId());
  }

  private boolean isExistsLostItem(int lostId) {
    return lostItemRepository.exists(Example.of(new LostItem(lostId)));
  }

  private LostItem saveLostItem(LostItemSaveCommand command) {
    LostItem lostItem = new LostItem(command);
    return lostItemRepository.save(lostItem);
  }

  private void verifyCoolTime(LostItemSaveCommand command) {
    lostItemRepository.findFirstByMemberIdOrderByCreatedAtDesc(command.lostMemberId())
        .filter(recent -> recent.getCreatedAt()
            .isAfter(LocalDateTime.now().minusHours(LOST_ITEM_COOL_TIME)))
        .ifPresent(recent -> {
          throw ItemError.LOST_SAVE_TIMEOUT.getGlobalException();
        });
  }

  private LostImage saveImage(LostItem item, UUID imageId) {
    LostImage image = itemImageRepository.findLostImageById(imageId)
        .orElseThrow(ItemError.LOST_IMAGE_NOT_FOUND::getGlobalException);
    image.setLostItem(item);
    return image;
  }

  private static void verifyImageCount(LostItemSaveCommand command) {
    if (command.images().size() > MAX_IMAGE_COUNT) {
      throw ItemError.LOST_MAX_IMAGE_COUNT_EXCEEDED.getGlobalException();
    }
  }
}
