package com.e205.member.service;

import com.e205.base.member.command.item.command.CreateItemCommand;
import com.e205.base.member.command.item.command.DeleteItemCommand;
import com.e205.base.member.command.item.command.ItemOrderCommand;
import com.e205.base.member.command.item.command.UpdateItemCommand;
import com.e205.base.member.command.item.command.UpdateItemOrderCommand;
import com.e205.base.member.command.item.payload.ItemPayload;
import com.e205.base.member.command.item.query.ReadItemNotInBagQuery;
import com.e205.base.member.command.item.service.ItemCommandService;
import com.e205.base.member.command.item.service.ItemQueryService;
import com.e205.member.dto.ChangeItemInfo;
import com.e205.member.dto.ChangeItemOrderRequest;
import com.e205.member.dto.CreateItemRequest;
import com.e205.member.dto.ItemResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {

  private final ItemCommandService itemCommandService;
  private final ItemQueryService itemQueryService;

  public void createItemInBag(CreateItemRequest request, Integer memberId) {
    CreateItemCommand command = request.toCommand(memberId);
    itemCommandService.save(command);
  }

  public List<ItemResponse> getAllItemsNotInBag(Integer memberId, Integer bagId) {
    ReadItemNotInBagQuery query = new ReadItemNotInBagQuery(memberId, bagId);
    List<ItemPayload> itemPayloads = itemQueryService.readItemsNotInBag(query);

    return itemPayloads.stream()
        .map(ItemResponse::from)
        .toList();
  }

  public void changeItemInfo(Integer memberId, Integer itemId, ChangeItemInfo changeItemInfo) {
    UpdateItemCommand command = changeItemInfo.toCommand(memberId, itemId);
    itemCommandService.update(command);
  }

  public void deleteItem(Integer memberId, Integer itemId) {
    DeleteItemCommand deleteItemCommand = new DeleteItemCommand(memberId, itemId);
    itemCommandService.delete(deleteItemCommand);
  }

  public void changeItemOrder(Integer memberId, List<ChangeItemOrderRequest> request) {
    List<ItemOrderCommand> items = request.stream()
        .map(req
            -> new ItemOrderCommand(req.itemId(), req.orderId().byteValue()))
        .toList();
    UpdateItemOrderCommand command = UpdateItemOrderCommand.builder()
        .memberId(memberId)
        .items(items)
        .build();
    itemCommandService.updateItemOrder(command);
  }
}
