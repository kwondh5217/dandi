package com.e205.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.e205.exception.dto.ErrorDetails;
import com.e205.exception.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @Mock
  private ExceptionLoader errorCodeManager;

  @InjectMocks
  private GlobalExceptionHandler globalExHandler;

  @BeforeEach
  void setUp() {
    given(errorCodeManager.getErrorDetails("E000"))
        .willReturn(new ErrorDetails("E000", "Test error message",
            HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  @Test
  void handleGlobalException_테스트() {
    // given
    GlobalException globalException = new GlobalException("E000");

    // when
    ResponseEntity<ErrorResponse> response = globalExHandler.handleGlobalException(globalException);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().code()).isEqualTo("E000");
    assertThat(response.getBody().message()).isEqualTo("Test error message");
  }
}
