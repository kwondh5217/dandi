package com.e205.base.member.command.member.command;

import lombok.Builder;

@Builder
public record ChangeAlarmSettingCommand(
    boolean comment,
    boolean lostItem,
    boolean foundItem,
    Integer memberId
) {

}
