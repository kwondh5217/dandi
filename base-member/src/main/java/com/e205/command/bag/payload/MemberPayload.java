package com.e205.command.bag.payload;

public record MemberPayload(
    Integer id,
    Integer bagId,
    String nickname,
    String email,
    EmailStatus status
) {

}

