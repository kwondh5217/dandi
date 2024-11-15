package com.e205.domain.exception;

import com.e205.exception.GlobalException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

@Getter
public enum MemberError {
  // Member & Email
  EMAIL_SEND_FAILED("E101", new IllegalStateException("이메일 전송 실패.")),
  USER_NOT_FOUND("E102", new EntityNotFoundException("유효하지 않은 사용자입니다.")),
  EMAIL_ALREADY_USED("E103", new IllegalArgumentException("이미 사용중인 이메일입니다.")),
  INVALID_EMAIL_FORMAT("E104", new IllegalArgumentException("유효하지 않은 이메일 형식입니다.")),
  INVALID_SIGNUP("E105", new IllegalStateException("신청하지 않은 회원가입 요청입니다.")),

  // Verification errors
  VERIFICATION_EXPIRED_OR_NOT_FOUND("E106", new IllegalArgumentException("인증 번호가 만료되었거나 존재하지 않습니다.")),
  VERIFICATION_NUMBER_INVALID("E107", new IllegalArgumentException("인증 번호가 올바르지 않습니다.")),
  VERIFICATION_TOKEN_INVALID("E108", new IllegalArgumentException("유효하지 않은 또는 만료된 토큰입니다.")),
  VERIFICATION_INFO_EXPIRED("E109", new IllegalArgumentException("만료된 인증 정보입니다.")),
  VERIFICATION_PROCESS_NOT_COMPLETE("E110", new IllegalStateException("이메일 인증을 하지 않았습니다.")),

  // Bag & Item
  MAX_BAG_COUNT_EXCEEDED("E111", new IllegalArgumentException("가방의 최대 개수를 초과했습니다.")),
  BAG_NAME_ALREADY_EXISTS("E112", new IllegalArgumentException("이미 사용 중인 가방 이름입니다.")),
  BAG_NOT_FOUND("E113", new EntityNotFoundException("가방을 찾을 수 없습니다.")),
  BAG_NOT_OWNED_BY_USER("E114", new IllegalStateException("해당 가방은 사용자의 소유가 아닙니다.")),
  CANNOT_DELETE_DEFAULT_BAG("E115", new IllegalArgumentException("기본 가방은 삭제할 수 없습니다.")),

  MAX_BAG_ITEM_COUNT_EXCEEDED("E116", new IllegalArgumentException("가방에 아이템을 더 추가할 수 없습니다.")),
  ITEM_NAME_ALREADY_EXISTS("E117", new IllegalArgumentException("이미 사용 중인 아이템 이름입니다.")),
  ITEM_COUNT_EXCEEDED("E118", new IllegalArgumentException("아이템 개수 제한을 초과했습니다.")),
  ITEM_NOT_FOUND("E119", new EntityNotFoundException("삭제할 아이템을 찾을 수 없습니다.")),
  ITEM_NOT_OWNED_BY_USER("E120", new IllegalStateException("해당 아이템은 사용자의 소유가 아닙니다.")),
  ;

  private String code;
  private Exception exception;

  MemberError(String code, RuntimeException e) {
    this.code = code;
    this.exception = e;
  }

  public GlobalException getGlobalException() {
    return new GlobalException(exception, code);
  }
}
