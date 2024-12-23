package com.e205.item.service;

import com.e205.base.item.command.LostItemDeleteCommand;
import com.e205.base.item.command.LostItemSaveCommand;
import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.LostItemListResponse;
import com.e205.item.dto.LostItemResponse;
import com.e205.base.item.payload.ItemImagePayload;
import com.e205.base.item.payload.LostItemPayload;
import com.e205.base.item.query.LostItemListQuery;
import com.e205.base.item.query.LostItemQuery;
import com.e205.base.item.service.LostItemCommandService;
import com.e205.base.item.service.LostItemQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    return makeResponse(payload);
  }

  @Transactional(readOnly = true)
  public LostItemListResponse getLostItems(Integer memberId) {
    LostItemListQuery query = new LostItemListQuery(memberId);
    return LostItemListResponse.from(lostItemQueryService.find(query).stream()
        .map(this::makeResponse)
        .toList());
  }

  private LostItemResponse makeResponse(LostItemPayload payload) {
    List<String> images = lostItemQueryService.findImages(payload.id()).stream()
        .map(ItemImagePayload::image).toList();
    return LostItemResponse.from(payload, images);
  }

  public boolean isCreatable(Integer memberId) {
    return lostItemQueryService.isCreatable(memberId);
  }
}
