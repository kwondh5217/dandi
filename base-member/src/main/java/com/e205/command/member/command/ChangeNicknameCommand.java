package com.e205.command.member.command;

public record ChangeNicknameCommand(
    Integer memberId,
    String nickname
) {

}
