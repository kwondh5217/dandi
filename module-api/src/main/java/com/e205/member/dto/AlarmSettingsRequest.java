package com.e205.member.dto;

import com.e205.command.member.command.ChangeAlarmSettingCommand;

public record AlarmSettingsRequest(
    boolean comment,
    boolean lostItem,
    boolean foundItem
) {
  public ChangeAlarmSettingCommand toCommand(Integer memberId) {
    return ChangeAlarmSettingCommand.builder()
        .comment(comment)
        .lostItem(lostItem)
        .foundItem(foundItem)
        .memberId(memberId)
        .build();
  }
}
