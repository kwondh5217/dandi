package com.e205.command.bag.payload;

import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;

public record MemberPayload(
    Integer id,
    Integer bagId,
    String nickname,
    String email,
    EmailStatus status,
    MemberStatus memberStatus
) {

}

