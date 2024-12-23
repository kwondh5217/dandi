package com.e205.config;

import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class NumberToSetConverter implements AttributeConverter<Set<Integer>, String> {

  @Override
  public Set<Integer> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return Set.of();
    }
    return Arrays.stream(dbData.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toSet());
  }

  @Override
  public String convertToDatabaseColumn(Set<Integer> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return "";
    }
    return attribute.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","));
  }
}
