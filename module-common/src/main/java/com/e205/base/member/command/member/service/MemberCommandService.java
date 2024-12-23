package com.e205.base.member.command.member.service;

import com.e205.base.member.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.base.member.command.member.command.MemberVerificationLinkCommand;
import com.e205.base.member.command.member.command.VerifyEmailAndRegisterCommand;
import com.e205.base.member.command.member.command.ChangeAlarmSettingCommand;
import com.e205.base.member.command.member.command.ChangeNicknameCommand;
import com.e205.base.member.command.member.command.ChangePasswordCommand;
import com.e205.base.member.command.member.command.CompleteSignUpCommand;
import com.e205.base.member.command.member.command.DeleteMemberCommand;
import com.e205.base.member.command.member.command.RegisterMemberCommand;
import com.e205.base.member.command.member.command.UpdateFcmCodeCommand;

public interface MemberCommandService {

  void requestEmailVerification(MemberVerificationLinkCommand requestEmailVerificationCommand);

  void registerMember(RegisterMemberCommand memberRegistrationCommand);

  void verifyEmailAndCompleteRegistration(
      VerifyEmailAndRegisterCommand verifyEmailAndRegisterCommand);

  void changePasswordWithVerificationNumber(
      ChangePasswordWithVerifNumber changePasswordWithVerifNumber
  );

  void completeSignUp(CompleteSignUpCommand command);

  void updateFcmCode(UpdateFcmCodeCommand command);

  void changePassword(ChangePasswordCommand command);

  void deleteMember(DeleteMemberCommand command);

  void changeNickname(ChangeNicknameCommand command);

  void changeAlarmSetting(ChangeAlarmSettingCommand command);
}
