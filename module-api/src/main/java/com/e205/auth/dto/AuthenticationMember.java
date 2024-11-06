package com.e205.auth.dto;

import lombok.Builder;

@Builder
public record AuthenticationMember(
    Integer id,
    String email,
    String password
) {

}
