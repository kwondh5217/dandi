package com.e205.command;

import java.util.UUID;

public record QuizSubmitCommand(
    Integer memberId,
    Integer quizId,
    UUID answerId
) {

}
