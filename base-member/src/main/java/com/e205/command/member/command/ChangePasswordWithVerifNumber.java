package com.e205.command.member.command;

public record ChangePasswordWithVerifNumber(
    String email,
    String verficationNumber,
    String newPassword
) {

}
