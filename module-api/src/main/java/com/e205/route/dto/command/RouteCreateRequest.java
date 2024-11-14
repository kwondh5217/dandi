package com.e205.route.dto.command;

import jakarta.validation.constraints.NotNull;

public record RouteCreateRequest(
    @NotNull(message = "가방 ID는 null 이 될 수 없습니다.")
    Integer bagId
) {

}
