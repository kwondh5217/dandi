package com.e205.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "새 비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "새 비밀번호는 {min}부터 {max}자 사이여야 합니다.")
    String newPassword,
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 {min}부터 {max}자 사이여야 합니다.")
    String pastPassword
) {

}