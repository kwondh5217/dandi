package com.e205.member.dto;

import com.e205.command.member.command.RegisterMemberCommand;

public record CreateMemberRequest(
    String nickname,
    String password,
    String email
) {
  public RegisterMemberCommand toCommand(String encryptedPassword) {
    return RegisterMemberCommand.builder()
        .nickname(nickname)
        .email(email)
        .password(encryptedPassword)
        .build();
  }
}
