package com.e205.base.noti;

import lombok.NonNull;

public record NotifyEvent(
    @NonNull Integer ownerId,
    @NonNull Integer senderId,
    @NonNull String type
) {

}
