package com.e205.command.member.command;

import lombok.Builder;

@Builder
public record MemberVerificationLinkCommand(
    String email
) {

}
