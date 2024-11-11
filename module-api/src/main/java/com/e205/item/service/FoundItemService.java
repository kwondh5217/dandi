package com.e205.item.service;

import com.e205.FoundItemType;
import com.e205.command.FoundItemDeleteCommand;
import com.e205.command.FoundItemSaveCommand;
import com.e205.exception.ItemError;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.item.dto.FoundItemListResponse;
import com.e205.item.dto.FoundItemResponse;
import com.e205.payload.FoundItemPayload;
import com.e205.payload.ItemImagePayload;
import com.e205.query.FoundItemListQuery;
import com.e205.query.FoundItemQuery;
import com.e205.service.FoundItemCommandService;
import com.e205.service.FoundItemQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FoundItemService {

  private final FoundItemCommandService foundItemCommandService;
  private final FoundItemQueryService foundItemQueryService;

  @Transactional
  public void save(int memberId, FoundItemCreateRequest request) {
    FoundItemSaveCommand command = request.toCommand(memberId);
    foundItemCommandService.save(command);
  }

  @Transactional
  public FoundItemResponse get(int memberId, int foundId) {
    FoundItemPayload payload = foundItemQueryService.find(
        new FoundItemQuery(memberId, foundId));
    return makeResponse(payload);
  }

  @Transactional(readOnly = true)
  public FoundItemListResponse getItems(int memberId) {
    FoundItemListQuery query = new FoundItemListQuery(memberId);
    return FoundItemListResponse.from(foundItemQueryService.find(query).stream()
        .map(this::makeResponse).toList());
  }

  @Transactional
  public void delete(int memberId, int foundId) {
    FoundItemDeleteCommand command = new FoundItemDeleteCommand(memberId, foundId);
    FoundItemPayload payload = foundItemQueryService.find(new FoundItemQuery(memberId, foundId));
    if (payload.memberId() != memberId) {
      ItemError.LOST_NOT_AUTH.throwGlobalException();
    }
    foundItemCommandService.delete(command);
  }

  @Transactional
  public FoundItemListResponse findReadable(int memberId) {
    return FoundItemListResponse.from(foundItemQueryService.findReadable(memberId).stream()
        .map(this::makeResponse).toList());
  }

  private FoundItemResponse makeResponse(FoundItemPayload payload) {
    if (payload.type() != FoundItemType.OTHER) {
      return FoundItemResponse.from(payload, null);
    }

    ItemImagePayload imagePayload = foundItemQueryService.findFoundItemImage(payload.id());
    return FoundItemResponse.from(payload, imagePayload.image());
  }
}
