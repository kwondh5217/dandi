package com.e205.member.dto;

import com.e205.base.member.command.bag.payload.MemberPayload;
import lombok.Builder;

@Builder
public record AlarmSettingResponse(
    boolean commentAlarm,
    boolean lostItemAlarm,
    boolean foundItemAlarm
) {

  public static AlarmSettingResponse from(MemberPayload payload) {
    return AlarmSettingResponse.builder()
        .foundItemAlarm(payload.foundItemAlarm())
        .lostItemAlarm(payload.lostItemAlarm())
        .commentAlarm(payload.commentAlarm())
        .build();
  }
}