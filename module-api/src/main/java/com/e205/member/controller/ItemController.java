package com.e205.member.controller;

import com.e205.auth.helper.AuthHelper;
import com.e205.member.dto.ChangeBagItemOrderRequest;
import com.e205.member.dto.ChangeItemInfo;
import com.e205.member.dto.ChangeItemOrderRequest;
import com.e205.member.dto.CreateItemRequest;
import com.e205.member.dto.ItemResponse;
import com.e205.member.service.ItemService;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/items")
@RestController
public class ItemController {

  private final ItemService itemService;
  private final AuthHelper authHelper;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createItemInBag(@Valid @RequestBody CreateItemRequest request) {
    itemService.createItemInBag(request, authHelper.getMemberId());
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/bags/{bagId}")
  public List<ItemResponse> getAllItemsNotInBag(@PathVariable Integer bagId) {
    return itemService.getAllItemsNotInBag(authHelper.getMemberId(), bagId);
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{itemId}")
  public void changeItemInfo(@PathVariable Integer itemId,
      @Valid @RequestBody ChangeItemInfo changeItemInfo) {
    itemService.changeItemInfo(authHelper.getMemberId(), itemId, changeItemInfo);
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{itemId}")
  public void deleteItem(@PathVariable Integer itemId) {
    itemService.deleteItem(authHelper.getMemberId(), itemId);
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping
  public void changeItemOrder(@RequestBody List<ChangeItemOrderRequest> request) {
    itemService.changeItemOrder(authHelper.getMemberId(), request);
  }
}
