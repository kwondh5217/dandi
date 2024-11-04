package com.e205.item.controller;

import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.item.dto.FoundItemResponse;
import com.e205.item.service.FoundItemService;
import java.security.Principal;
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

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createFoundItem(
      Principal principal,
      @RequestPart("foundItemRequest") FoundItemCreateRequest request,
      @RequestPart("image") MultipartFile image
  ) {
    int memberId = Integer.parseInt(principal.getName());
    foundItemService.save(memberId, request, image);
  }

  @GetMapping("/{foundId}")
  public ResponseEntity<FoundItemResponse> getFoundItem(
      Principal principal,
      @PathVariable int foundId
  ) {
    int memberId = Integer.parseInt(principal.getName());
    FoundItemResponse response = foundItemService.get(memberId, foundId);
    return ResponseEntity.ok(response);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{foundId}")
  public void deleteFoundItem(
      Principal principal,
      @PathVariable int foundId
  ) {
    int memberId = Integer.parseInt(principal.getName());
    foundItemService.delete(memberId, foundId);
  }
}
