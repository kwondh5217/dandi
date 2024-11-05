package com.e205.member.dto;

import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import lombok.Builder;

public record PasswordResetRequest(
    String verificationNumber,
    String email,
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
