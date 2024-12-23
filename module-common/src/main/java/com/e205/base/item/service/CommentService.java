package com.e205.base.item.service;

import com.e205.base.item.payload.CommentPayload;
import com.e205.base.item.query.CommentListQuery;
import com.e205.base.item.query.CommentQuery;
import com.e205.base.item.command.CommentCreateCommand;
import java.util.List;

public interface CommentService {

  void createComment(CommentCreateCommand command);

  List<CommentPayload> findComments(CommentListQuery query);

  CommentPayload findComment(CommentQuery query);
}
