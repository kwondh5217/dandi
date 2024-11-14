package com.e205.member.dto;

import com.e205.command.member.payload.EmailStatus;
import com.e205.command.bag.payload.MemberPayload;
import lombok.Builder;

@Builder
public record MemberInfoResponse(
    Integer id,
    String nickname,
    EmailStatus emailStatus,
    String email,
    Integer bagId
) {

  public static MemberInfoResponse from(MemberPayload payload) {
    return MemberInfoResponse.builder()
        .id(payload.id())
        .nickname(payload.nickname())
        .emailStatus(payload.status())
        .email(payload.email())
        .bagId(payload.bagId())
        .build();
  }
}
