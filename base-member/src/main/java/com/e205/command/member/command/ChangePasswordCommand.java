package com.e205.command.member.command;

public record ChangePasswordCommand(
    Integer memberId,
    String newPassword,
    String pastPassword
) {

}
