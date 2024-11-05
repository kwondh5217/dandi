package com.e205.member.service;

import com.e205.auth.helper.AuthHelper;
import com.e205.command.FoundItemSaveCommand;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.bag.query.FindMemberQuery;
import com.e205.command.member.command.ChangePasswordWithVerifNumber;
import com.e205.command.member.command.CheckVerificationNumberCommand;
import com.e205.command.member.command.CreateVerificationNumberCommand;
import com.e205.command.member.command.RegisterMemberCommand;
import com.e205.command.member.command.RequestEmailVerificationCommand;
import com.e205.command.member.service.EmailCommandService;
import com.e205.command.member.service.MemberCommandService;
import com.e205.command.member.service.MemberQueryService;
import com.e205.domain.member.entity.Member;
import com.e205.item.dto.FoundItemCreateRequest;
import com.e205.member.dto.AuthEmailLinkRequest;
import com.e205.member.dto.CheckVerificationNumberRequest;
import com.e205.member.dto.CreateMemberRequest;
import com.e205.member.dto.MemberInfoResponse;
import com.e205.member.dto.PasswordNumberEmailRequest;
import com.e205.member.dto.PasswordResetRequest;
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
  public void saveMember(CreateMemberRequest request) {
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
}
