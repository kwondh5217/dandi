package com.e205.base.member.command.member.command;

public record ChangeNicknameCommand(
    Integer memberId,
    String nickname
) {

}
