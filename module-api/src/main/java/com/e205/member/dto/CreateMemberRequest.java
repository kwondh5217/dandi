package com.e205.member.dto;

import com.e205.base.member.command.member.command.RegisterMemberCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 1, max = 15, message = "아이템은 {min} 부터 {max}자 사이여야 합니다.")
    String nickname,
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "새 비밀번호는 {min}부터 {max}자 사이여야 합니다.")
    String password,
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
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
