package com.e205.member.controller;

import com.e205.command.member.service.MemberCommandService;
import com.e205.command.member.service.MemberQueryService;
import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.QuizResponse;
import com.e205.member.dto.AuthEmailLinkRequest;
import com.e205.member.dto.CheckVerificationNumberRequest;
import com.e205.member.dto.CreateMemberRequest;
import com.e205.member.dto.MemberInfoResponse;
import com.e205.member.dto.PasswordNumberEmailRequest;
import com.e205.member.dto.PasswordResetRequest;
import com.e205.member.service.MemberService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class MemberController {

  private final MemberService memberService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void createMember(@RequestBody CreateMemberRequest request) {
    memberService.saveMember(request);
  }

  @GetMapping
  public ResponseEntity<MemberInfoResponse> getMemberInfo() {
    MemberInfoResponse memberInfo = memberService.getMemberInfo();
    return ResponseEntity.ok(memberInfo);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/email")
  public void requestAuthEmail(@RequestBody AuthEmailLinkRequest request) {
    memberService.requestAuthLink(request);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/password")
  public void requestPasswordChangeNumber(@RequestBody PasswordNumberEmailRequest request) {
    memberService.requestPasswordChangeNumber(request);
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/password")
  public void changePassword(@RequestBody PasswordResetRequest request) {
    memberService.resetPassword(request);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/verification")
  public void checkVerificationNumber(@RequestBody CheckVerificationNumberRequest request) {
    memberService.checkVerificationNumber(request);
  }
}
