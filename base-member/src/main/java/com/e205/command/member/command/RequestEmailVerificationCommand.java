package com.e205.command.member.command;

import lombok.Builder;

@Builder
public record RequestEmailVerificationCommand(
    Integer userId,
    String email
) {

}
