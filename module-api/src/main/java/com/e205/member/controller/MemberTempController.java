package com.e205.member.controller;

import com.e205.auth.jwt.JwtProvider;
import com.e205.command.bag.payload.EmailStatus;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth/manager")
@RestController
public class MemberTempController {

  private final MemberRepository memberRepository;
  private final BagRepository bagRepository;
  private final JwtProvider jwtProvider;

  @PostMapping("/{nickname}")
  public String createOrGetTempMember(@PathVariable String nickname) {
    Optional<Member> existingMember = memberRepository.findByEmail(nickname + "@example.com");

    if (existingMember.isPresent()) {
      String token = jwtProvider.generateAccessToken(existingMember.get().getId());
      return "Bearer " + token;

    } else {
      Member tempMember = Member.builder()
          .nickname(nickname)
          .password("TempPass123!")
          .email(nickname + "@example.com")
          .status(EmailStatus.VERIFIED)
          .build();

      Member savedMember = memberRepository.save(tempMember);

      Bag tempBag = Bag.builder()
          .memberId(savedMember.getId())
          .enabled('Y')
          .bagOrder((byte) 1)
          .name(nickname + "Bag")
          .build();
      bagRepository.save(tempBag);

      String token = jwtProvider.generateAccessToken(savedMember.getId());
      return "Bearer " + token;
    }
  }
}