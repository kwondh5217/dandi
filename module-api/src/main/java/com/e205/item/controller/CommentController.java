package com.e205.item.controller;

import com.e205.base.item.CommentType;
import com.e205.item.dto.CommentResponse;
import com.e205.item.service.CommentApiService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentApiService commentApiService;

  @GetMapping("/comments/{commentId}")
  public ResponseEntity<CommentResponse> getComment(
      @PathVariable
      int commentId,
      @Pattern(regexp = "lostComment|foundComment",
      message = "type은 lostComment나 foundComment만 가능합니다.")
      @RequestParam("type")
      String type
  ) {
    CommentType commentType = switch (type) {
      case "foundComment" -> CommentType.FOUND;
      case "lostComment" -> CommentType.LOST;
      default -> null;
    };

    return ResponseEntity.ok(commentApiService.findComment(commentType, commentId));
  }
}
