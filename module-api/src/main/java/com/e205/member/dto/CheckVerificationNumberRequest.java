package com.e205.member.dto;

import com.e205.base.member.command.member.command.CheckVerificationNumberCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckVerificationNumberRequest(
    @NotBlank(message = "인증번호는 공백일 수 없습니다.")
    @Size(min = 6, max = 6, message = "인증번호는 {min}자리여야 합니다.")
    String verificationNumber,
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email
) {
  public CheckVerificationNumberCommand toCommand() {
    return CheckVerificationNumberCommand.builder()
        .verificationNumber(verificationNumber)
        .email(email)
        .build();
  }
}
