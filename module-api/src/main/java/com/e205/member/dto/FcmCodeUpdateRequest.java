package com.e205.member.dto;

import jakarta.validation.constraints.NotBlank;

public record FcmCodeUpdateRequest(
    @NotBlank(message = "fcm 코드는 공백일 수 없습니다.")
    String fcmCode
) {

}
