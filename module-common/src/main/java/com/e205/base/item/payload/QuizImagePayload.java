package com.e205.base.item.payload;

/**
 * 퀴즈 옵션 이미지 정보
 * @param image 이미지의 이름
 * @param description 이미지 설명
 */
public record QuizImagePayload(
    String image,
    String description
) {

}
