package com.e205.member.dto;

import com.e205.base.member.command.member.command.ChangePasswordWithVerifNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
    @NotBlank(message = "인증번호는 공백일 수 없습니다.")
    @Size(min = 6, max = 6, message = "인증번호는 {min}자리여야 합니다.")
    String verificationNumber,
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    @NotBlank(message = "새 비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "새 비밀번호는 {min}부터 {max}자 사이여야 합니다.")
    String newPassword
) {
  public ChangePasswordWithVerifNumber toCommand(String encryptedPassword) {
    return ChangePasswordWithVerifNumber.builder()
        .verificationNumber(verificationNumber)
        .email(email)
        .newPassword(encryptedPassword)
        .build();
  }
}
