package com.e205.base.member.command.member.command;

public record SendVerificationEmailCommand(
    String toEmail,
    String token
) {

}
