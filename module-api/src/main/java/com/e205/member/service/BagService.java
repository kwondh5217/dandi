package com.e205.member.service;

import com.e205.auth.helper.AuthHelper;
import com.e205.command.bag.command.AddItemsToBagCommand;
import com.e205.command.bag.command.BagDeleteCommand;
import com.e205.command.bag.command.BagItemDeleteCommand;
import com.e205.command.bag.command.BagItemOrderCommand;
import com.e205.command.bag.command.BagItemOrderUpdateCommand;
import com.e205.command.bag.command.BagNameUpdateCommand;
import com.e205.command.bag.command.BagOrderCommand;
import com.e205.command.bag.command.BagOrderUpdateCommand;
import com.e205.command.bag.command.CopyBagCommand;
import com.e205.command.bag.command.CreateBagCommand;
import com.e205.command.bag.command.SelectBagCommand;
import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
import com.e205.command.bag.query.ReadAllBagItemsQuery;
import com.e205.command.bag.query.ReadAllBagsQuery;
import com.e205.command.bag.query.ReadAllItemInfoQuery;
import com.e205.command.bag.service.BagCommandService;
import com.e205.command.bag.service.BagQueryService;
import com.e205.command.item.payload.ItemPayload;
import com.e205.command.member.service.MemberQueryService;
import com.e205.domain.member.entity.Member;
import com.e205.member.dto.AddItemsToBagRequest;
import com.e205.member.dto.BagOrderChangeRequest;
import com.e205.member.dto.BagResponse;
import com.e205.member.dto.ChangeBagItemOrderRequest;
import com.e205.member.dto.ChangeBagNameRequest;
import com.e205.member.dto.CopySelectBagRequest;
import com.e205.member.dto.CreateBagRequest;
import com.e205.member.dto.CreateItemRequest;
import com.e205.member.dto.DeleteBagRequest;
import com.e205.member.dto.ItemResponse;
import com.e205.member.dto.ReadBagRequest;
import com.e205.member.dto.SelectBagRequest;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BagService {

  private final MemberQueryService memberQueryService;
  private final BagCommandService bagCommandService;
  private final BagQueryService bagQueryService;

  @Transactional
  public void createBag(CreateBagRequest request) {
    CreateBagCommand command = request.toCommand();
    bagCommandService.save(command);
  }

  public List<BagResponse> readBags(Integer memberId) {
    ReadAllBagsQuery query = new ReadAllBagsQuery(memberId);
    List<BagPayload> bagPayloads = bagQueryService.readAllBags(query);

    return bagPayloads.stream()
        .map(BagResponse::from)
        .toList();
  }

  public void changeBagOrder(List<BagOrderChangeRequest> request, Integer memberId) {
    List<BagOrderCommand> bagCommands = request.stream()
        .map(BagOrderChangeRequest::toCommand)
        .toList();

    BagOrderUpdateCommand command = BagOrderUpdateCommand.builder()
        .memberId(memberId)
        .bags(bagCommands)
        .build();

    bagCommandService.updateBagOrder(command);
  }

  public void selectBag(SelectBagRequest request) {
    MemberPayload member = memberQueryService.findMember(new FindMemberQuery(request.memberId()));

    SelectBagCommand command = request.toCommmand(member.bagId());
    bagCommandService.selectBag(command);
  }

  public void copySelectBag(CopySelectBagRequest request) {
    CopyBagCommand command = request.toCommand();
    bagCommandService.copyBag(command);
  }

  public void changeBagName(ChangeBagNameRequest request) {
    BagNameUpdateCommand command = request.toCommand();
    bagCommandService.updateBagName(command);
  }

  public List<ItemResponse> readBagItems(ReadBagRequest request) {
    ReadAllBagItemsQuery query = new ReadAllBagItemsQuery(request.memberId(), request.bagId());
    List<BagItemPayload> bagItemPayloads = bagQueryService.readAllBagItemsByBagId(query);

    List<Integer> itemIds = bagItemPayloads.stream()
        .map(BagItemPayload::itemId)
        .distinct()
        .toList();

    ReadAllItemInfoQuery itemQuery = new ReadAllItemInfoQuery(itemIds);
    List<ItemPayload> itemPayloads = bagQueryService.readAllByItemIds(itemQuery);

    Map<Integer, ItemPayload> itemPayloadMap = itemPayloads.stream()
        .collect(Collectors.toMap(ItemPayload::id, item -> item));

    return bagItemPayloads.stream()
        .map(bagItem ->
            ItemResponse.from(bagItem, itemPayloadMap.get(bagItem.itemId())))
        .toList();
  }

  public void deleteBag(DeleteBagRequest request) {
    MemberPayload member = memberQueryService.findMember(
        new FindMemberQuery(request.memberId()));
    BagDeleteCommand command = request.toCommand(request.memberId(), member.bagId());

    bagCommandService.delete(command);
  }

  public void deleteItemInBag(Integer bagId, Integer itemId, Integer memberId) {
    BagItemDeleteCommand command = new BagItemDeleteCommand(memberId, bagId, itemId);
    bagCommandService.deleteBagItem(command);
  }

  public void changeOrderItemInBag(Integer bagId, List<ChangeBagItemOrderRequest> requests,
      Integer memberId) {
    List<BagItemOrderCommand> items = requests.stream()
        .map(request -> new BagItemOrderCommand(
            request.itemId(),
            request.orderId().byteValue()
        ))
        .toList();
    BagItemOrderUpdateCommand command = new BagItemOrderUpdateCommand(memberId, bagId, items);
    bagCommandService.updateBagItemOrder(command);
  }

  public void addItemsToBag(AddItemsToBagRequest request) {
    AddItemsToBagCommand command = request.toCommand();
    bagCommandService.addItemToBag(command);
  }
}
