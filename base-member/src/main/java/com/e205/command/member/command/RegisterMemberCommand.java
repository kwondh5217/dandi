package com.e205.command.member.command;

public record RegisterMemberCommand(
    String email,
    String password,
    String nickname
) {

}