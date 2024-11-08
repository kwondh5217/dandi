package com.e205.member.controller;

import com.e205.auth.jwt.JwtProvider;
import com.e205.domain.member.entity.Member;
import com.e205.member.service.MemberTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth/manager")
@RestController
public class MemberTempController {

  private final MemberTempService memberTempService;
  private final JwtProvider jwtProvider;

  @PostMapping("/{nickname}")
  public String createOrGetTempMember(@PathVariable String nickname) {
    Member savedMember = memberTempService.generateTempMember(nickname);
    String token = jwtProvider.generateAccessToken(savedMember.getId());
    return "Bearer " + token;
  }
}