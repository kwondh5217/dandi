package com.e205.exception.dto;

public record ErrorDetails(
    String code,
    String message,
    int httpStatus
) {

}
