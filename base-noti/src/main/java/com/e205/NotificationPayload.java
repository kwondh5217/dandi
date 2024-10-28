package com.e205;

import java.time.LocalDateTime;

public record NotificationPayload(
    Integer id,
    Integer memberId,
    Integer lostItemId,
    Integer foundItemId,
    Integer routeId,
    Integer commentId,
    LocalDateTime createdAt,
    char confirmation,
    String title
) {

}
