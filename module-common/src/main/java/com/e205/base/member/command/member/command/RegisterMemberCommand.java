package com.e205.base.member.command.member.command;

import lombok.Builder;

@Builder
public record RegisterMemberCommand(
    String email,
    String password,
    String nickname
) {

}