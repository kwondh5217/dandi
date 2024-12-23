package com.e205.member.dto;

import com.e205.base.member.command.member.command.CreateVerificationNumberCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordNumberEmailRequest(
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email
) {
  public CreateVerificationNumberCommand toCommand(String email) {
    return new CreateVerificationNumberCommand(email);
  }
}
