package com.e205.member.dto;

import com.e205.base.member.command.item.command.UpdateItemCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeItemInfo(
    @NotBlank(message = "이모티콘은 공백일 수 없습니다.")
    @Pattern(regexp = "^\\X{1}$", message = "이모티콘은 반드시 1자여야 합니다.")
    String emoticon,
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 1, max = 20, message = "아이템은 {min} 부터 {max}자 사이여야 합니다.")
    String name,
    @NotNull(message = "색깔은 공백일 수 없습니다.")
    Byte colorKey
) {

  public UpdateItemCommand toCommand(Integer memberId, Integer itemId) {
    return UpdateItemCommand.builder()
        .itemId(itemId)
        .memberId(memberId)
        .emoticon(emoticon)
        .name(name)
        .colorKey(colorKey)
        .build();
  }
}
