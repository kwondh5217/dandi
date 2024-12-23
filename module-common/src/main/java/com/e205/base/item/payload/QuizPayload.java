package com.e205.base.item.payload;

import java.util.List;

/**
 * 퀴즈를 풀기 위한 정보
 * @param id 퀴즈 식별자
 * @param foundId 퀴즈 대상 습득물 식별자
 * @param options 퀴즈 옵션 목록
 */
public record QuizPayload(
    Integer id,
    Integer foundId,
    List<QuizImagePayload> options
) {

}
