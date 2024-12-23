package com.e205.item.service;

import static java.util.stream.Collectors.groupingBy;

import com.e205.base.item.CommentType;
import com.e205.base.item.command.CommentCreateCommand;
import com.e205.base.member.command.bag.payload.MemberPayload;
import com.e205.base.member.command.bag.query.FindMemberQuery;
import com.e205.base.member.command.member.query.FindMembersByIdQuery;
import com.e205.base.member.command.member.service.MemberQueryService;
import com.e205.item.dto.CommentListResponse;
import com.e205.item.dto.CommentQueryRequest;
import com.e205.item.dto.CommentResponse;
import com.e205.base.item.payload.CommentPayload;
import com.e205.base.item.query.CommentQuery;
import com.e205.base.item.service.CommentService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentApiService {

  private final MemberQueryService memberQueryService;
  private final CommentService commentService;

  public CommentListResponse findComments(CommentQueryRequest request, int itemId,
      CommentType type) {
    List<CommentPayload> comments = commentService.findComments(request.toQuery(type, itemId));

    return convertResponse(comments);
  }

  public void createComment(CommentCreateCommand command) {
    commentService.createComment(command);
  }

  public CommentResponse findComment(CommentType type, int commentId) {
    var comment = commentService.findComment(new CommentQuery(null, type, null, null, commentId));
    String nickname = memberQueryService.findMember(new FindMemberQuery(comment.writerId()))
        .nickname();
    return CommentResponse.from(comment, nickname);
  }

  private CommentListResponse convertResponse(List<CommentPayload> comments) {
    List<Integer> writerIds = comments.stream().map(CommentPayload::writerId).toList();
    Map<Integer, List<MemberPayload>> collectToId = memberQueryService.findMembers(
        new FindMembersByIdQuery(writerIds)).stream().collect(groupingBy(MemberPayload::id));

    Function<CommentPayload, String> commentToNickname = comment -> collectToId.get(
        comment.writerId()).get(0).nickname();

    List<CommentResponse> responses = comments.stream()
        .map(comment -> CommentResponse.from(comment, commentToNickname.apply(comment))).toList();
    return new CommentListResponse(responses);
  }
}
