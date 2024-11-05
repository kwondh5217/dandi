package com.e205.command.member.payload;

import com.e205.command.bag.payload.EmailStatus;

public record MemberAuthPayload(
    Integer id,
    Integer bagId,
    String nickname,
    String email,
    String password,
    EmailStatus status
)  {

}
