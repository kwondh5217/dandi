package com.e205.item.dto;

import com.e205.base.item.FoundItemType;
import com.e205.base.item.command.FoundItemSaveCommand;
import com.e205.geo.dto.Point;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FoundItemCreateRequest(
    @NotNull(message = "습득물의 종류가 필요합니다.")
    FoundItemType category,
    @NotNull(message = "습득한 장소가 필요합니다.")
    Point foundLocation,
    @NotNull(message = "이미지는 필수입니다.")
    String image,
    @PastOrPresent(message = "습득날짜는 미래일 수 없습니다.")
    LocalDateTime foundAt,
    @NotBlank(message = "저장 위치 묘사는 필수입니다.")
    @Size(min = 1, max = 255, message = "길이는 {min} ~ {max} 사이여야 합니다.")
    String storageDesc,
    @NotBlank(message = "물건 묘사는 필수입니다.")
    @Size(min = 1, max = 255, message = "길이는 {min} ~ {max} 사이여야 합니다.")
    String itemDesc
) {
  public FoundItemSaveCommand toCommand(Integer memberId, String address) {

    String newStorageDesc = storageDesc.replaceAll("\\n{3}", "\n\n");
    String newItemDesc = itemDesc.replaceAll("\\n{3}", "\n\n");

    return FoundItemSaveCommand.builder()
        .foundAt(foundAt)
        .itemDesc(newItemDesc)
        .storageDesc(newStorageDesc)
        .type(category)
        .location(foundLocation.toGeoPoint())
        .address(address)
        .memberId(memberId)
        .image(image)
        .build();
  }
}
