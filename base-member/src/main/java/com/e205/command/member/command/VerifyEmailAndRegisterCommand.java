package com.e205.command.member.command;

import lombok.Builder;

@Builder
public record VerifyEmailAndRegisterCommand(
    String email,
    String token
) {

}
