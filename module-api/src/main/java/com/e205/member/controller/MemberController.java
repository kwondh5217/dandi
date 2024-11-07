package com.e205.member.controller;

import com.e205.auth.helper.AuthHelper;
import com.e205.member.dto.ChangePasswordRequest;
import com.e205.member.dto.FcmCodeUpdateRequest;
import com.e205.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MemberController {

  private final MemberService memberService;
  private final AuthHelper authHelper;

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/fcm")
  public void updateFcmCode(@RequestBody FcmCodeUpdateRequest request) {
    memberService.updateFcmCode(authHelper.getMemberId(), request.fcmCode());
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/password")
  public void updatePassword(@RequestBody ChangePasswordRequest request) {
    memberService.changePassword(authHelper.getMemberId(), request.newPassword(),
        request.pastPassword());
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping
  public void deleteMember(@RequestBody ChangePasswordRequest request) {
    memberService.deleteMember(authHelper.getMemberId());
  }
}
