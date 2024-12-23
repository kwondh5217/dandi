package com.e205.base.member.command.member.service;

import com.e205.base.member.command.member.command.CheckVerificationNumberCommand;
import com.e205.base.member.command.member.command.CreateVerificationNumberCommand;
import com.e205.base.member.command.member.command.SendVerificationEmailCommand;
import com.e205.base.member.command.member.command.CreateEmailTokenCommand;

public interface EmailCommandService {

  String createAndStoreToken(CreateEmailTokenCommand createEmailTokenCommand);

  void sendVerificationEmail(SendVerificationEmailCommand sendVerificationEmailCommand);

  void createAndStoreVerificationNumber(
      CreateVerificationNumberCommand createVerificationNumberCommand);

  void checkVerificationNumber(CheckVerificationNumberCommand checkVerificationNumberCommand);
}
