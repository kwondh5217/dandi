package com.e205.exception;

import com.e205.exception.dto.ErrorDetails;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ExceptionLoader {

  private static final String DELIMITER = ",";

  private final Map<String, ErrorDetails> errorCodeMap = new ConcurrentHashMap<>();

  @PostConstruct
  public void loadErrorCodes() throws IOException {
    ClassPathResource csv = new ClassPathResource("/csv/error-codes.csv");
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(csv.getInputStream()))) {
      errorCodeMap.putAll(reader.lines()
          .skip(1) // 헤더
          .map(line -> line.split(DELIMITER))
          .filter(parts -> parts.length == 3)
          .collect(Collectors.toMap(
              parts -> parts[0],
              parts -> new ErrorDetails(parts[0], parts[1], Integer.parseInt(parts[2]))
          ))
      );
    }
  }

  public ErrorDetails getErrorDetails(String code) {
    ErrorDetails details = errorCodeMap.get(code);
    if (details == null) {
      return errorCodeMap.get("E000");
    }
    return details;
  }
}
