package com.e205.member.dto;

import com.e205.command.member.command.ChangeAlarmSettingCommand;
import com.e205.command.member.payload.AlarmType;

public record AlarmSettingsRequest(
    boolean enabled,
    AlarmType target
) {

  public ChangeAlarmSettingCommand toCommand(Integer memberId) {
    return ChangeAlarmSettingCommand.builder()
        .enabled(enabled)
        .memberId(memberId)
        .target(target)
        .build();
  }
}
