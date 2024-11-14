package com.e205;

import java.time.LocalDateTime;
import lombok.NonNull;

public record NotificationPayload(
    @NonNull Integer id,
    @NonNull Integer memberId,
    Integer lostItemId,
    Integer foundItemId,
    Integer routeId,
    Integer commentId,
    LocalDateTime createdAt,
    char confirmation,
    String title
) {

}
