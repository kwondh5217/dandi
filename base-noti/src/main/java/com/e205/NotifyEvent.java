package com.e205;

public record NotifyEvent(
    Integer ownerId,
    Integer senderId,
    String type
) {

}
