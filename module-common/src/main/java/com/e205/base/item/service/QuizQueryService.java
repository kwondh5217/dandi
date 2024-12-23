package com.e205.base.item.service;

import com.e205.base.item.query.QuizQuery;
import com.e205.base.item.payload.QuizPayload;

public interface QuizQueryService {

  QuizPayload findQuiz(QuizQuery query);

  boolean getQuizResult(Integer memberId, Integer quizId);
}
