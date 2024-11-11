package com.e205.member.controller;

import com.e205.auth.helper.AuthHelper;
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
import com.e205.member.service.BagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/bags")
@RestController
public class BagController {

  private final BagService bagService;
  private final AuthHelper authHelper;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createBag(@RequestBody CreateBagRequest request) {
    bagService.createBag(new CreateBagRequest(authHelper.getMemberId(), request.name()));
  }

  @GetMapping
  public ResponseEntity<List<BagResponse>> readBags() {
    return ResponseEntity.ok(bagService.readBags(authHelper.getMemberId()));
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping
  public void changeBagOrder(@RequestBody List<BagOrderChangeRequest> request) {
    bagService.changeBagOrder(request, authHelper.getMemberId());
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{bagId}")
  public void selectBag(@PathVariable Integer bagId) {
    bagService.selectBag(new SelectBagRequest(bagId, authHelper.getMemberId()));
  }

  @PostMapping("/{bagId}")
  public void copySelectBag(@PathVariable Integer bagId,
      @RequestBody CopySelectBagRequest request) {
    bagService.copySelectBag(
        new CopySelectBagRequest(bagId, request.newBagName(), authHelper.getMemberId()));
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{bagId}")
  public void changeBagName(@PathVariable Integer bagId,
      @RequestBody ChangeBagNameRequest request) {
    bagService.changeBagName(
        new ChangeBagNameRequest(bagId, request.name(), authHelper.getMemberId()));
  }

  @GetMapping("/{bagId}")
  public ResponseEntity<List<ItemResponse>> readBagItems(@PathVariable Integer bagId) {
    return ResponseEntity.ok(
        bagService.readBagItems(new ReadBagRequest(bagId, authHelper.getMemberId())));
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{bagId}")
  public void deleteBag(@PathVariable Integer bagId) {
    bagService.deleteBag(new DeleteBagRequest(bagId, authHelper.getMemberId()));
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{bagId}/items/{itemId}")
  public void deleteItemInBag(@PathVariable Integer bagId, @PathVariable Integer itemId) {
    bagService.deleteItemInBag(bagId, itemId, authHelper.getMemberId());
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{bagId}/items")
  public void addItemsToBag(@PathVariable Integer bagId, @RequestBody List<Integer> itemIds) {
    bagService.addItemsToBag(new AddItemsToBagRequest(bagId, authHelper.getMemberId(), itemIds));
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{bagId}/items")
  public void changeOrderItemInBag(@PathVariable Integer bagId,
      @RequestBody List<ChangeBagItemOrderRequest> request) {
    bagService.changeOrderItemInBag(bagId, request, authHelper.getMemberId());
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{bagId}/items")
  public void removeItemsInBag(@PathVariable Integer bagId,
      @RequestBody List<Integer> itemIds) {
    bagService.removeItemsInBag(bagId, itemIds, authHelper.getMemberId());
  }
}
