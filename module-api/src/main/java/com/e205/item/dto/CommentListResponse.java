package com.e205.item.dto;

import com.e205.payload.CommentPayload;
import java.util.List;

public record CommentListResponse(
    List<CommentResponse> payloads
) {

}
