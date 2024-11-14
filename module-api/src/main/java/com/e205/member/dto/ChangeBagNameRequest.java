package com.e205.member.dto;

import com.e205.command.bag.command.BagNameUpdateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeBagNameRequest(
    @NotNull(message = "bagId는 null일 수 없습니다.")
    Integer bagId,
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 1, max = 20, message = "이름은 {min} 부터 {max}자 사이여야 합니다.")
    String name,
    Integer memberId
) {
  public BagNameUpdateCommand toCommand() {
    return BagNameUpdateCommand.builder()
        .bagId(bagId)
        .name(name)
        .memberId(memberId)
        .build();
  }
}
