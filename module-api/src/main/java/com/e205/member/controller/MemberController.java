package com.e205.member.controller;

import com.e205.auth.helper.AuthHelper;
import com.e205.item.dto.FoundItemListResponse;
import com.e205.item.service.FoundItemService;
import com.e205.member.dto.ChangeNicknameRequest;
import com.e205.member.dto.ChangePasswordRequest;
import com.e205.item.dto.LostItemListResponse;
import com.e205.item.service.LostItemService;
import com.e205.member.dto.FcmCodeUpdateRequest;
import com.e205.member.dto.MemberInfoResponse;
import com.e205.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final LostItemService lostItemService;
  private final FoundItemService foundItemService;

  @GetMapping
  public ResponseEntity<MemberInfoResponse> getMemberInfo() {
    MemberInfoResponse memberInfo = memberService.getMemberInfo();
    return ResponseEntity.ok(memberInfo);
  }

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
  @PutMapping("/nickname")
  public void updateNickname(@RequestBody ChangeNicknameRequest request) {
    memberService.changeNickname(authHelper.getMemberId(), request.nickname());
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping
  public void deleteMember(@RequestBody ChangePasswordRequest request) {
    memberService.deleteMember(authHelper.getMemberId());
  }

  @GetMapping("/losts")
  public ResponseEntity<LostItemListResponse> findMemberLostItems(
      @AuthenticationPrincipal(expression = "id") Integer memberId
  ) {
    return ResponseEntity.ok(lostItemService.getLostItems(memberId));
  }

  @GetMapping("/founds")
  public ResponseEntity<FoundItemListResponse> findMemberFoundItems(
      @AuthenticationPrincipal(expression = "id") Integer memberId
  ) {
    return ResponseEntity.ok(foundItemService.getItems(memberId));
  }
}
