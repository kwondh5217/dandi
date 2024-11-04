package com.e205.command.member.command;

public record RequestEmailVerificationCommand(
    Integer userId,
    String email
) {

}
