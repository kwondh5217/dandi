package com.e205.member.service;

import com.e205.auth.helper.AuthHelper;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
import com.e205.command.member.command.ChangePasswordCommand;
import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.CheckVerificationNumberCommand;
import com.e205.command.member.command.CompleteSignUpCommand;
import com.e205.command.member.command.CreateVerificationNumberCommand;
import com.e205.command.member.command.DeleteMemberCommand;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.command.member.command.RequestEmailVerificationCommand;
import com.e205.command.member.command.UpdateFcmCodeCommand;
import com.e205.command.member.command.VerifyEmailAndRegisterCommand;
import com.e205.command.member.service.EmailCommandService;
import com.e205.command.member.service.MemberCommandService;
import com.e205.command.member.service.MemberQueryService;
import com.e205.member.dto.AuthEmailLinkRequest;
import com.e205.member.dto.CheckVerificationNumberRequest;
import com.e205.member.dto.CompleteSignUpRequest;
import com.e205.member.dto.CreateMemberRequest;
import com.e205.member.dto.FcmCodeUpdateRequest;
import com.e205.member.dto.MemberInfoResponse;
import com.e205.member.dto.PasswordNumberEmailRequest;
import com.e205.member.dto.PasswordResetRequest;
import com.e205.member.dto.VerifyEmailRequest;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberCommandService memberCommandService;
  private final PasswordEncoder passwordEncoder;
  private final MemberQueryService memberQueryService;
  private final AuthHelper authHelper;
  private final EmailCommandService emailCommandService;

  @Transactional
  public void registerMember(CreateMemberRequest request) {
    String encryptedPassword = passwordEncoder.encode(request.password());
    RegisterMemberCommand command = request.toCommand(encryptedPassword);
    memberCommandService.registerMember(command);
  }

  public MemberInfoResponse getMemberInfo() {
    Integer memberId = authHelper.getMemberId();
    MemberPayload member = memberQueryService.findMember(new FindMemberQuery(memberId));
    return MemberInfoResponse.from(member);
  }

  public void requestAuthLink(AuthEmailLinkRequest request) {
    Integer memberId = authHelper.getMemberId();
    RequestEmailVerificationCommand command = request.toCommand(memberId);
    memberCommandService.requestEmailVerification(command);
  }

  public void requestPasswordChangeNumber(PasswordNumberEmailRequest request) {
    CreateVerificationNumberCommand command = request.toCommand(request.email());
    emailCommandService.createAndStoreVerificationNumber(command);
  }

  public void resetPassword(PasswordResetRequest request) {
    String encryptedPassword = passwordEncoder.encode(request.newPassword());
    ChangePasswordWithVerifNumber command = request.toCommand(encryptedPassword);

    memberCommandService.changePasswordWithVerificationNumber(command);
  }

  public void checkVerificationNumber(CheckVerificationNumberRequest request) {
    CheckVerificationNumberCommand command = request.toCommand();
    emailCommandService.checkVerificationNumber(command);
  }

  public void verifyEmail(VerifyEmailRequest request) {
    VerifyEmailAndRegisterCommand command = request.toCommand();
    memberCommandService.verifyEmailAndCompleteRegistration(command);
  }

  public void completeSignUp(CompleteSignUpRequest request) {
    CompleteSignUpCommand command = request.toCommand();
    memberCommandService.completeSignUp(command);
  }

  public void updateFcmCode(Integer memberId, String fcmCode) {
    UpdateFcmCodeCommand command = new UpdateFcmCodeCommand(fcmCode, memberId);
    memberCommandService.updateFcmCode(command);
  }

  public void changePassword(Integer memberId, String newPassword, String pastPassword) {
    String beforePassword = memberQueryService.checkPastPassword(memberId);
    System.out.println("beforePassword " + beforePassword);
    System.out.println("pastPassword " + pastPassword);
    if (!passwordEncoder.matches(pastPassword, beforePassword)) {
      throw new IllegalArgumentException("이전 비밀번호가 일치하지 않습니다.");
    }
    String encryptedPassword = passwordEncoder.encode(newPassword);
    ChangePasswordCommand command = new ChangePasswordCommand(memberId, encryptedPassword,
        pastPassword);
    memberCommandService.changePassword(command);
  }

  public void deleteMember(Integer memberId) {
    DeleteMemberCommand command = new DeleteMemberCommand(memberId);
    memberCommandService.deleteMember(command);
  }
}
