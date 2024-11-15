package com.e205.item.dto;

import com.e205.command.LostItemSaveCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record LostItemCreateRequest(
    @NotBlank(message = "공백일 수 없습니다.")
    @Size(min=1, max=255, message = "길이는 {min}~{max} 사이여야 합니다.")
    String situationDesc,
    @NotBlank(message = "공백일 수 없습니다.")
    @Size(min=1, max=255, message = "길이는 {min}~{max} 사이여야 합니다.")
    String itemDesc,
    @Size(max=3, message = "이미지는 {max}개 이하여야 합니다..")
    List<String> images,
    @NotNull
    Integer startRoute,
    Integer endRoute,
    @PastOrPresent(message = "분실 시간은 미래일 수 없습니다.")
    LocalDateTime lostAt) {

  public LostItemSaveCommand toCommand(Integer memberId) {
    String newSituationDesc = situationDesc.replaceAll("\\n{3}", "\n\n");
    String newItemDesc = itemDesc.replaceAll("\\n{3}", "\n\n");
    return LostItemSaveCommand.builder().lostMemberId(memberId).situationDesc(newSituationDesc)
        .itemDesc(newItemDesc).images(images).startRouteId(startRoute).endRouteId(endRoute)
        .lostAt(lostAt).build();
  }
}
