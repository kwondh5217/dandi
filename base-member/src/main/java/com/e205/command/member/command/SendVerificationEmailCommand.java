package com.e205.command.member.command;

public record SendVerificationEmailCommand(
    String toEmail,
    String token
) {

}
