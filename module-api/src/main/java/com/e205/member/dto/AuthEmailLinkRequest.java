package com.e205.member.dto;

import com.e205.command.member.command.RequestEmailVerificationCommand;

public record AuthEmailLinkRequest(
    String email
) {
  public RequestEmailVerificationCommand toCommand(Integer memberId) {
    return RequestEmailVerificationCommand.builder()
        .userId(memberId)
        .email(email)
        .build();
  }
}
