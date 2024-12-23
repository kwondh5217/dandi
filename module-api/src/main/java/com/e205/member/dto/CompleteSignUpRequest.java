package com.e205.member.dto;

import com.e205.base.member.command.member.command.CompleteSignUpCommand;

public record CompleteSignUpRequest(
    String email
) {

  public CompleteSignUpCommand toCommand() {
    return new CompleteSignUpCommand(email);
  }
}
