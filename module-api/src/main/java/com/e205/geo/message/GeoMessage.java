package com.e205.geo.message;

public enum GeoMessage {
  NOT_FOUND("NOT FOUND"),
  RESPONSE("response"),
  RESULT("result"),
  STATUS("status"),
  POINT("point"),
  KEY("key"),

  BLANK(""),
  COMMA(","),

  FAILED_TO_PARSE("좌표 변환에 실패하였습니다."),
  NOT_BE_NULL("NULL 이 될 수 없습니다."),
  NONE_ADDRESS("주소 없음");
  ;

  private final String symbol;

  GeoMessage(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }
}
