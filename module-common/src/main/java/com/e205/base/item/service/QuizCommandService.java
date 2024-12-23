package com.e205.base.item.service;

import com.e205.base.item.command.QuizMakeCommand;
import com.e205.base.item.command.QuizSubmitCommand;

public interface QuizCommandService {

  void make(QuizMakeCommand command);

  void submit(QuizSubmitCommand command);
}