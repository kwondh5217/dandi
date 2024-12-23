package com.e205.member.dto;

import com.e205.base.member.command.member.command.MemberVerificationLinkCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthEmailLinkRequest(
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email
) {
  public MemberVerificationLinkCommand toCommand() {
    return MemberVerificationLinkCommand.builder()
        .email(email)
        .build();
  }
}
