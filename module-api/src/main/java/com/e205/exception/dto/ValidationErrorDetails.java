package com.e205.exception.dto;

import java.util.List;

public record ValidationErrorDetails(
    String code,
    List<FieldError> errorDetails
) {

}
