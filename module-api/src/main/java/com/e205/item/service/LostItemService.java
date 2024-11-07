package com.e205.item.service;

import com.e205.command.LostItemDeleteCommand;
import com.e205.command.LostItemSaveCommand;
import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.LostItemResponse;
import com.e205.payload.ItemImagePayload;
import com.e205.payload.LostItemPayload;
import com.e205.query.LostItemQuery;
import com.e205.service.LostItemCommandService;
import com.e205.service.LostItemQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class LostItemService {

  private final LostItemCommandService lostItemCommandService;
  private final LostItemQueryService lostItemQueryService;

  @Transactional
  public void createLostItem(Integer memberId, LostItemCreateRequest request) {
    LostItemSaveCommand command = request.toCommand(memberId);
    lostItemCommandService.save(command);
  }

  @Transactional
  public void finishLostItem(Integer memberId, Integer lostItemId) {
    LostItemPayload payload = lostItemQueryService.find(
        new LostItemQuery(memberId, lostItemId));

    if (!payload.memberId().equals(memberId)) {
      throw new RuntimeException("분실물을 종료할 권한이 없습니다.");
    }

    lostItemCommandService.delete(new LostItemDeleteCommand(lostItemId));
  }

  @Transactional
  public LostItemResponse getLostItem(Integer memberId, Integer lostItemId) {
    LostItemQuery query = new LostItemQuery(memberId, lostItemId);
    LostItemPayload payload = lostItemQueryService.find(query);

    List<String> images = lostItemQueryService.findImages(lostItemId).stream()
        .map(ItemImagePayload::image).toList();

    return LostItemResponse.from(payload, images);
  }
}
