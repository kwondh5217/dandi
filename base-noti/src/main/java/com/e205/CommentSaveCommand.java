package com.e205;

import java.util.Set;

public record CommentSaveCommand(
    Integer commentId,
    Integer writerId,
    Set<Integer> senders,
    String type
){}
