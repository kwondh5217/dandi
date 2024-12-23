package com.e205.member.dto;

import com.e205.base.member.command.member.command.VerifyEmailAndRegisterCommand;

public record VerifyEmailRequest(
    String email,
    String token
) {

  public VerifyEmailAndRegisterCommand toCommand() {
    return VerifyEmailAndRegisterCommand.builder()
        .email(email)
        .token(token)
        .build();
  }
}
