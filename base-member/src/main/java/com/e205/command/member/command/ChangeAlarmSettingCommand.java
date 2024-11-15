package com.e205.command.member.command;

import com.e205.command.member.payload.AlarmType;
import lombok.Builder;

@Builder
public record ChangeAlarmSettingCommand(
    boolean comment,
    boolean lostItem,
    boolean foundItem,
    Integer memberId
) {

}
