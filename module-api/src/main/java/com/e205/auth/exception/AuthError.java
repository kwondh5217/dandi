package com.e205.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthError {
  FAILED_AUTHENTICATION("E001", "인증에 실패하였습니다."),
  FAILED_VERIFY_TOKEN("E002", "검증할 수 없는 토큰입니다."),
  IS_EXPIRED_TOKEN("E003", "만료된 토큰입니다.");

  private String code;
  private String message;
}
