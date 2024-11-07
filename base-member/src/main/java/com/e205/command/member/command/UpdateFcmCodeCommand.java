package com.e205.command.member.command;

import lombok.Builder;

@Builder
public record UpdateFcmCodeCommand(
    String fcmCode,
    Integer memberId
) {

}
