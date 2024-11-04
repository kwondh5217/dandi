package com.e205.command.member.command;

public record CreateEmailTokenCommand(
    Integer userId,
    String email
) {

}
