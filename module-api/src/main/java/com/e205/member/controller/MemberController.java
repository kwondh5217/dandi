package com.e205.member.controller;

import com.e205.command.member.service.MemberCommandService;
import com.e205.item.dto.LostItemCreateRequest;
import com.e205.item.dto.QuizResponse;
import com.e205.member.dto.CreateMemberRequest;
import com.e205.member.service.MemberService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public void createMember(@RequestBody CreateMemberRequest createMemberRequest) {
    memberService.saveMember(createMemberRequest);
  }

}
