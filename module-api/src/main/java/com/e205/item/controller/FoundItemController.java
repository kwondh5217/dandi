package com.e205.item.controller;

import com.e205.auth.helper.AuthHelper;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.item.dto.FoundItemResponse;
import com.e205.item.service.FoundItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/founds")
@RestController
public class FoundItemController {

  private final FoundItemService foundItemService;
  private final AuthHelper authHelper;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createFoundItem(
      @RequestPart("foundItemRequest") FoundItemCreateRequest request,
      @RequestPart("image") MultipartFile image
  ) {
    foundItemService.save(authHelper.getMemberId(), request, image);
  }

  @GetMapping("/{foundId}")
  public ResponseEntity<FoundItemResponse> getFoundItem(
      @PathVariable int foundId
  ) {
    FoundItemResponse response = foundItemService.get(authHelper.getMemberId(), foundId);
    return ResponseEntity.ok(response);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{foundId}")
  public void deleteFoundItem(
      @PathVariable int foundId
  ) {
    foundItemService.delete(authHelper.getMemberId(), foundId);
  }
}
