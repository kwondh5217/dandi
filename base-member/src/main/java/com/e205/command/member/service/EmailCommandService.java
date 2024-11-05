package com.e205.command.member.service;

import com.e205.command.member.command.CheckEmailProgressCommand;
import com.e205.command.member.command.CheckVerificationNumberCommand;
import com.e205.command.member.command.CreateEmailTokenCommand;
import com.e205.command.member.command.CreateVerificationNumberCommand;
import com.e205.command.member.command.SendVerificationEmailCommand;
import com.e205.command.member.command.VerifyEmailToken;

public interface EmailCommandService {

  String createAndStoreToken(CreateEmailTokenCommand createEmailTokenCommand);

  void sendVerificationEmail(SendVerificationEmailCommand sendVerificationEmailCommand);

  void checkEmailVerificationInProgress(CheckEmailProgressCommand checkEmailProgressCommandString);

  String verifyToken(VerifyEmailToken verifyEmailToken);

  void createAndStoreVerificationNumber(
      CreateVerificationNumberCommand createVerificationNumberCommand);

  void checkVerificationNumber(CheckVerificationNumberCommand checkVerificationNumberCommand);
}
