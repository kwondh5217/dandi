package com.e205.command.member.command;

public record CheckVerificationNumberCommand(
    String email,
    String verificationNumber
) {

}
