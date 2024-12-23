package com.e205.base.member.command.member.command;

import lombok.Builder;

@Builder
public record ChangePasswordWithVerifNumber(
    String email,
    String verificationNumber,
    String newPassword
) {

}
