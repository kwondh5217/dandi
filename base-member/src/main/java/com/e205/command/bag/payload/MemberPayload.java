package com.e205.command.bag.payload;

import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;
import lombok.Builder;

@Builder
public record MemberPayload(
    Integer id,
    Integer bagId,
    String nickname,
    String email,
    EmailStatus status,
    MemberStatus memberStatus,
    String fcmCode,
    boolean foundItemAlarm,
    boolean lostItemAlarm,
    boolean commentAlarm
) {

}
