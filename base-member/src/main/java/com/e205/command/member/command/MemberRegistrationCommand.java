package com.e205.command.member.command;

public record MemberRegistrationCommand(
    String email,
    String password,
    String nickname
) {

}