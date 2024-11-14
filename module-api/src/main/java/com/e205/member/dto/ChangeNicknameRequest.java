package com.e205.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeNicknameRequest(
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 1, max = 15, message = "아이템은 {min} 부터 {max}자 사이여야 합니다.")
    String nickname
) {

}
