package com.e205.exception.dto;

public record FieldError(
    String field,
    String reason
) {

}
