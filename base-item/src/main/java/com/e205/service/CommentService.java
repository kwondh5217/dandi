package com.e205.service;

import com.e205.command.CommentCreateCommand;
import com.e205.payload.CommentPayload;
import com.e205.query.CommentListQuery;
import java.util.List;

public interface CommentService {

  void createComment(CommentCreateCommand command);

  List<CommentPayload> findComments(CommentListQuery query);
}
