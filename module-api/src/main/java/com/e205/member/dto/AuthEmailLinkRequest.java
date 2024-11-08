package com.e205.member.dto;

import com.e205.command.member.command.MemberVerificationLinkCommand;

public record AuthEmailLinkRequest(
    String email
) {
  public MemberVerificationLinkCommand toCommand() {
    return MemberVerificationLinkCommand.builder()
        .email(email)
        .build();
  }
}
