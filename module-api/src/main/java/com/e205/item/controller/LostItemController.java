package com.e205.item.controller;

import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.LostItemResponse;
import com.e205.item.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/losts")
@RestController
public class LostItemController {

  private final LostItemService lostItemService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createLostItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @RequestBody LostItemCreateRequest request) {
    lostItemService.createLostItem(memberId, request);
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{lostId}")
  public void finishLostItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @PathVariable int lostId) {
    lostItemService.finishLostItem(memberId, lostId);
  }

  @GetMapping("/{lostId}")
  public ResponseEntity<LostItemResponse> getLostItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @PathVariable int lostId) {
    LostItemResponse response = lostItemService.getLostItem(memberId, lostId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
