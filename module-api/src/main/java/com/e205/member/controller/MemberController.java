package com.e205.member.controller;

import com.e205.auth.helper.AuthHelper;
import com.e205.member.dto.FcmCodeUpdateRequest;
import com.e205.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;
  private final AuthHelper authHelper;

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/member/fcm")
  public void updateFcmCode(@RequestBody FcmCodeUpdateRequest request) {
    memberService.updateFcmCode(authHelper.getMemberId(), request.fcmCode());
  }
}
