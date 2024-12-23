package com.e205.base.member.command.member.command;

public record ChangePasswordCommand(
    Integer memberId,
    String newPassword,
    String pastPassword
) {

}
