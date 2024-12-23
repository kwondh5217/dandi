package com.e205.base.item.command;

import java.util.UUID;

public record QuizMakeCommand(
    Integer foundItemId,
    Integer memberId,
    UUID answerId
) {

}
