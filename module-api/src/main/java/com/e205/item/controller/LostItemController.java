package com.e205.item.controller;

import com.e205.CommentType;
import com.e205.item.dto.CommentCreateRequest;
import com.e205.item.dto.CommentListResponse;
import com.e205.item.dto.CommentQueryRequest;
import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.LostItemResponse;
import com.e205.item.service.CommentApiService;
import com.e205.item.service.LostItemService;
import com.e205.payload.CommentPayload;
import com.e205.query.CommentListQuery;
import com.e205.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/losts")
@RestController
public class LostItemController {

  private final LostItemService lostItemService;
  private final CommentApiService commentService;

  @GetMapping("/creatable")
  public void canCreateLostItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId
  ) {
    lostItemService.isCreatable(memberId);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createLostItem(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @Valid @RequestBody LostItemCreateRequest request) {
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

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{lostId}/comments")
  public void createLostItemComment(
      @AuthenticationPrincipal(expression = "id") Integer memberId,
      @Valid @RequestBody CommentCreateRequest request,
      @PathVariable int lostId
  ) {
    commentService.createComment(request.toCommand(memberId, lostId, CommentType.LOST));
  }

  @GetMapping("/{lostId}/comments")
  public ResponseEntity<CommentListResponse> findComments(
      @Valid @ModelAttribute CommentQueryRequest request,
      @PathVariable int lostId
  ) {
    return ResponseEntity.ok(commentService.findComments(request, lostId, CommentType.LOST));
  }
}
