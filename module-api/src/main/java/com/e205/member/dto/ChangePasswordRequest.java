package com.e205.member.dto;

public record ChangePasswordRequest(
    String newPassword,
    String pastPassword
) {

}
