package com.e205.service;

import com.e205.entity.Quiz;
import com.e205.query.QuizQuery;

public interface QuizQueryService {

  Quiz findQuiz(QuizQuery query);
}
