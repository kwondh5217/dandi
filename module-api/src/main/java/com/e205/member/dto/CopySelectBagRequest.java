package com.e205.member.dto;

import com.e205.command.bag.command.CopyBagCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CopySelectBagRequest(
    Integer bagId,
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 1, max = 20, message = "이름은 {min} 부터 {max}자 사이여야 합니다.")
    String newBagName,
    Integer memberId
) {
  public CopyBagCommand toCommand() {
    return CopyBagCommand.builder()
        .bagsId(bagId)
        .memberId(memberId)
        .newBagName(newBagName)
        .build();
  }
}
