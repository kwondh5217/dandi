package com.e205.service;

import com.e205.command.QuizMakeCommand;
import com.e205.command.QuizSubmitCommand;

public interface QuizCommandService {

  void make(QuizMakeCommand command);

  void submit(QuizSubmitCommand command);
}