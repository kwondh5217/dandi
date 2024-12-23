package com.e205.item.controller;

import com.e205.base.item.CommentType;
import com.e205.base.item.command.CommentCreateCommand;
import com.e205.item.dto.CommentCreateRequest;
import com.e205.item.dto.CommentListResponse;
import com.e205.item.dto.CommentQueryRequest;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.item.dto.FoundItemListResponse;
import com.e205.item.dto.FoundItemResponse;
import com.e205.item.service.CommentApiService;
import com.e205.item.service.FoundItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/founds")
@RestController
public class FoundItemController {

  private final FoundItemService foundItemService;
  private final CommentApiService commentService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createFoundItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @Valid @RequestBody FoundItemCreateRequest request
  ) {
    foundItemService.save(memberId, request);
  }

  @GetMapping
  public ResponseEntity<FoundItemListResponse> getReadable(
      @AuthenticationPrincipal(expression = "id") Integer memberId
  ) {
    return ResponseEntity.ok(foundItemService.findReadable(memberId));
  }

  @GetMapping("/{foundId}")
  public ResponseEntity<FoundItemResponse> getFoundItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @PathVariable int foundId
  ) {
    FoundItemResponse response = foundItemService.get(memberId, foundId);
    return ResponseEntity.ok(response);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{foundId}")
  public void deleteFoundItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @PathVariable int foundId
  ) {
    foundItemService.delete(memberId, foundId);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{foundId}/comments")
  public void createComment(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @Valid @RequestBody CommentCreateRequest request,
      @PathVariable Integer foundId
  ) {
    CommentCreateCommand command = request.toCommand(memberId, foundId, CommentType.FOUND);
    commentService.createComment(command);
  }

  @GetMapping("/{foundId}/comments")
  public ResponseEntity<CommentListResponse> findComment(
      @Valid @ModelAttribute CommentQueryRequest request,
      @PathVariable Integer foundId
  ) {
    return ResponseEntity.ok(commentService.findComments(request, foundId, CommentType.FOUND));
  }
}
