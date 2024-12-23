package com.e205.member.dto;

import com.e205.base.member.command.bag.command.CreateBagCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBagRequest(
    Integer memberId,
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 1, max = 20, message = "이름은 {min} 부터 {max}자 사이여야 합니다.")
    String name
) {

  public CreateBagCommand toCommand() {
    return CreateBagCommand.builder()
        .memberId(memberId)
        .name(name)
        .build();
  }
}

