package com.e205.member.dto;

import com.e205.command.member.command.CheckVerificationNumberCommand;

public record CheckVerificationNumberRequest(
    String verificationNumber,
    String email
) {
  public CheckVerificationNumberCommand toCommand() {
    return CheckVerificationNumberCommand.builder()
        .verificationNumber(verificationNumber)
        .email(email)
        .build();
  }
}
