package com.e205.item.controller;

import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.LostItemResponse;
import com.e205.item.service.LostItemService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/losts")
@RestController
public class LostItemController {

  private final LostItemService lostItemService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createLostItem(
      Principal principal,
      @RequestPart("lostItemRequest")LostItemCreateRequest request,
      @RequestPart("images") List<MultipartFile> images
  ) {
    int memberId = Integer.parseInt(principal.getName());
    lostItemService.createLostItem(memberId, request, images);
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{lostId}")
  public void finishLostItem(Principal principal, @PathVariable int lostId) {
    int memberId = Integer.parseInt(principal.getName());
    lostItemService.finishLostItem(memberId, lostId);
  }

  @GetMapping("/{lostId}")
  public ResponseEntity<LostItemResponse> getLostItem(Principal principal, @PathVariable int lostId) {
    int memberId = Integer.parseInt(principal.getName());
    LostItemResponse response = lostItemService.getLostItem(memberId, lostId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
