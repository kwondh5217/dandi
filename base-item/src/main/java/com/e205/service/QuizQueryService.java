package com.e205.service;

import com.e205.payload.QuizPayload;
import com.e205.query.QuizQuery;

public interface QuizQueryService {

  QuizPayload findQuiz(QuizQuery query);
}
