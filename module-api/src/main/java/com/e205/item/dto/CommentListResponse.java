package com.e205.item.dto;

import java.util.List;

public record CommentListResponse(
    List<CommentResponse> payloads
) {

}
