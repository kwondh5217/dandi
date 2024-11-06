package com.e205.command.member.service;

import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.command.member.command.RequestEmailVerificationCommand;
import com.e205.command.member.command.VerifyEmailAndRegisterCommand;

public interface MemberCommandService {

  void requestEmailVerification(RequestEmailVerificationCommand requestEmailVerificationCommand);

  void registerMember(RegisterMemberCommand memberRegistrationCommand);

  void verifyEmailAndCompleteRegistration(
      VerifyEmailAndRegisterCommand verifyEmailAndRegisterCommand);

  void changePasswordWithVerificationNumber(
      ChangePasswordWithVerifNumber changePasswordWithVerifNumber
  );
}
