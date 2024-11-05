package com.e205.member.dto;

import com.e205.command.member.command.CreateVerificationNumberCommand;

public record PasswordNumberEmailRequest(
    String email
) {
  public CreateVerificationNumberCommand toCommand(String email) {
    return new CreateVerificationNumberCommand(email);
  }
}
