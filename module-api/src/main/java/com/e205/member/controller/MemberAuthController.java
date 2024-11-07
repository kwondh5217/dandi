package com.e205.member.controller;

import com.e205.member.dto.AuthEmailLinkRequest;
import com.e205.member.dto.CheckVerificationNumberRequest;
import com.e205.member.dto.CompleteSignUpRequest;
import com.e205.member.dto.CreateMemberRequest;
import com.e205.member.dto.MemberInfoResponse;
import com.e205.member.dto.PasswordNumberEmailRequest;
import com.e205.member.dto.PasswordResetRequest;
import com.e205.member.dto.VerifyEmailRequest;
import com.e205.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class MemberAuthController {

  private final MemberService memberService;

  @ResponseStatus(HttpStatus.OK)
  @PostMapping
  public void registerMember(@RequestBody CreateMemberRequest request) {
    memberService.registerMember(request);
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
  @PutMapping("/password")
  public void changePassword(@RequestBody PasswordResetRequest request) {
    memberService.resetPassword(request);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/verification")
  public void checkVerificationNumber(@RequestBody CheckVerificationNumberRequest request) {
    memberService.checkVerificationNumber(request);
  }

  @GetMapping("/verify")
  public ResponseEntity<String> verifyEmail(
      @RequestParam("email") String email,
      @RequestParam("token") String token) {
    memberService.verifyEmail(new VerifyEmailRequest(email, token));
    String responseHtml = "<html><body>" +
        "<script>window.close();</script>" +
        "</body></html>";
    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(responseHtml);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/verify")
  public void completeSignUp(@RequestBody CompleteSignUpRequest request) {
    memberService.completeSignUp(request);
  }
}
