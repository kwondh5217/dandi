package com.e205.auth.dto;

public record LoginRequest(
    String email,
    String password
) {

}
