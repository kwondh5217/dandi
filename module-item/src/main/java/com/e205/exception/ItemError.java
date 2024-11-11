package com.e205.exception;

import lombok.Getter;

@Getter
public enum ItemError {
  // lost
  LOST_NOT_FOUND("E301", new IllegalStateException("분실물이 존재하지 않습니다.")),
  LOST_SAVE_TIMEOUT("E302", new IllegalStateException("제한 시간 내에 분실물을 등록할 수 없습니다.")),
  LOST_IMAGE_NOT_FOUND("E303", new IllegalStateException("분실물 이미지가 존재하지 않습니다.")),
  LOST_MAX_IMAGE_COUNT_EXCEEDED("E304", new IllegalArgumentException("이미지 제한 개수를 초과했습니다.")),
  LOST_ALREADY_ENDED("E305", new IllegalStateException("이미 종료된 분실물입니다.")),
  LOST_NOT_VALID_POSITION("E306", new IllegalStateException("조회할 수 있는 범위를 벗어났습니다.")),
  LOST_NOT_AUTH("E307", new IllegalStateException("분실물을 조회할 권한이 없습니다.")),


  // found
  FOUND_AT_FUTURE("E401", new IllegalArgumentException("습득 날짜가 미래입니다.")),
  FOUND_OTHER_REQUIRE_IMAGE("E402", new IllegalArgumentException("이미지는 필수입니다.")),
  FOUND_CARD_NOT_REQUIRE_IMAGE("E403", new IllegalArgumentException("카드나 신분증 사진이 포함되어 있습니다.")),
  FOUND_NOT_AUTH("E404", new IllegalStateException("습득물을 조회할 권한이 없습니다.")),
  FOUND_IMAGE_NOT_FOUND("E405", new IllegalStateException("이미지가 존재하지 않습니다.")),
  FOUND_ALREADY_ENDED("E406", new IllegalStateException("이미 종료된 습득물입니다.")),
  FOUND_QUIZ_NOT_SOLVED("E407", new IllegalStateException("퀴즈를 풀지 않았습니다.")),
  FOUND_QUIZ_ALREADY_SOLVED("E408", new IllegalStateException("이미 퀴즈를 풀었습니다.")),
  FOUND_QUIZ_NOT_FOUND("E409", new IllegalStateException("퀴즈가 존재하지 않습니다.")),
  FOUND_NOT_EXIST("E410", new IllegalStateException("습득물이 존재하지 않습니다.")),
  FOUND_QUIZ_NOT_AUTH("E411", new IllegalStateException("퀴즈를 생성할 권한이 없습니다.")),
  FOUND_QUIZ_IMAGE_INSUFFICIENT("E412", new IllegalStateException("퀴즈를 생성할 이미지가 부족합니다.")),
  FOUND_QUIZ_OWNER_CANNOT_SOLVE("E413", new IllegalStateException("퀴즈를 생성한 사람은 퀴즈를 풀 수 없습니다.")),


  // image
  IMAGE_EXT_NOT_VALID("E601", new IllegalArgumentException("이미지 확장자가 잘못되었습니다.")),
  IMAGE_TYPE_NOT_VALID("E602", new IllegalArgumentException("이미지 타입이 잘못되었습니다.")),
  IMAGE_SAVE_FAIL("E603", new IllegalStateException("이미지 저장에 실패했습니다.")),

  COMMENT_NOT_EXIST("E701", new IllegalStateException("댓글이 존재하지 않습니다.")),

  ;

  private String code;
  private Exception exception;

  ItemError(String code, RuntimeException e) {
    this.code = code;
    this.exception = e;
  }

  public GlobalException getGlobalException() {
    return new GlobalException(exception, code);
  }

  public void throwGlobalException() {
    throw new GlobalException(exception, code);
  }
}
